package org.asmus.builder;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.digitizer.AxisDigitizer;
import org.asmus.digitizer.TriggerDigitizer;
import org.asmus.introspect.impl.BothIntrospector;
import org.asmus.introspect.impl.PushIntrospector;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.mapper.GamepadStateMapper;
import org.asmus.model.ButtonClick;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import org.asmus.qualifier.impl.AutoLongClickQualifier;
import org.asmus.qualifier.impl.ImmediateQualifier;
import org.asmus.qualifier.impl.ModifierAndLongPressQualifier;
import org.asmus.qualifier.impl.MultiplicityQualifier;
import org.asmus.tool.AxisMapper;
import org.asmus.tool.EventMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IntrospectedEventFactory {
    private final Sinks.Many<GamepadEvent> qualifiedEventStream = Sinks.many().multicast().directBestEffort();

    static Predicate<Map.Entry<String, Integer>> notZeroFor(String axisName) {
        return q -> q.getKey().equals(axisName) && q.getValue() != 0;
    }

    private final ActuationBehaviour MODIFIER = ActuationBehaviour.builder()
            .introspector(new ReleaseIntrospector())
            .qualifier(new ModifierAndLongPressQualifier())
            .build();

    private final ActuationBehaviour LONG = ActuationBehaviour.builder()
            .introspector(new BothIntrospector())
            .qualifier(new AutoLongClickQualifier())
            .build();

    private final ActuationBehaviour PUSH = ActuationBehaviour.builder()
            .introspector(new PushIntrospector())
            .qualifier(new ImmediateQualifier())
            .build();

    private final ActuationBehaviour MULTIPLICITY = ActuationBehaviour.builder()
            .introspector(new BothIntrospector())
            .qualifier(new MultiplicityQualifier())
            .build();

    List<ActuationBehaviour> behaviours = List.of(MODIFIER, LONG, PUSH, MULTIPLICITY);

    Predicate<Map.Entry<String, Integer>> onlyDpValues = q -> q.getKey().contains("dp");

    Consumer<ButtonClick> qualify = c -> behaviours.forEach(q -> Optional.ofNullable(c)
            .map(q.getIntrospector()::translate)
//            .map(p -> p.withModifiers(MODIFIER.getIntrospector().getModifiersResetEvents()))
            .ifPresent(q.getQualifier().useStream(qualifiedEventStream)::qualify));

    public OsDevice getButtonStream() {
        GamepadStateMapper gamepadStateMapper = new GamepadStateMapper();
        return states -> states.stream()
                .map(gamepadStateMapper::map)
                .forEach(qualify);
    }

    public RawArrowSource getArrowsStream() {
        return axisStates -> {
            List<GamepadEvent> vertical = axisStates.entrySet().stream()
                    .filter(onlyDpValues)
                    .filter(notZeroFor(EButtonAxisMapping.UP.getMapping()))
                    .map(AxisMapper.mapVertical)
                    .toList();

            List<GamepadEvent> horizontal = axisStates.entrySet().stream()
                    .filter(onlyDpValues)
                    .filter(notZeroFor(EButtonAxisMapping.LEFT.getMapping()))
                    .map(AxisMapper.mapHorizontal)
                    .toList();

            Flux.merge(Flux.fromIterable(vertical), Flux.fromIterable(horizontal))
                    .map(q -> q.withModifiers(
                            MODIFIER.getIntrospector().getModifiersResetEvents().stream()
                                    .map(EButtonAxisMapping::getByMappingName)
                                    .collect(Collectors.toSet())
                    ))
                    .subscribe(qualifiedEventStream::tryEmitNext);
        };
    }

    static Predicate<TriggerPosition> edgeValue = q -> Math.abs(q.getPosition()) == TriggerDigitizer.MAX;

    public RawArrowSource rightTriggerStream() {
        return genericDigitizedTriggerProcessor(EButtonAxisMapping.TRIGGER_RIGHT);
    }

    public RawArrowSource leftTriggerStream() {
        return genericDigitizedTriggerProcessor(EButtonAxisMapping.TRIGGER_LEFT);
    }

    public RawArrowSource leftStickStream() {
        return genericDigitizedStickAxisProcessor(EButtonAxisMapping.LEFT_STICK_X, EButtonAxisMapping.LEFT_STICK_Y);
    }

    public RawArrowSource rightStickStream() {
        return genericDigitizedStickAxisProcessor(EButtonAxisMapping.RIGHT_STICK_X, EButtonAxisMapping.RIGHT_STICK_Y);
    }

    RawArrowSource genericDigitizedStickAxisProcessor(EButtonAxisMapping x, EButtonAxisMapping y) {
        AxisDigitizer digitizer = new AxisDigitizer(qualifiedEventStream);
        Map<EButtonAxisMapping, Integer> mem = new HashMap<>();
        return q -> {
            Integer xVal = q.get(x.getMapping());
            Integer yVal = q.get(y.getMapping());

            if (still(x, mem).test(xVal) && still(y, mem).test(yVal))
                return;

            Optional.of(Map.of(x.getMapping(), xVal, y.getMapping(), yVal))
                    .map(EventMapper.translateAxis(x.getMapping(), y.getMapping()))
                    .map(EventMapper.translateAxisMove)
                    .ifPresent(digitizer.digitize(x));
        };
    }

    Predicate<Integer> still(EButtonAxisMapping ax, Map<EButtonAxisMapping, Integer> mem) {
        return q -> {
            Integer prev = mem.put(ax, q);

            if (prev == null) return false;

            return prev.equals(q);
        };
    }

    RawArrowSource genericDigitizedTriggerProcessor(EButtonAxisMapping axisMapping) {
        TriggerDigitizer digitizer = new TriggerDigitizer(qualifiedEventStream);
        Map<EButtonAxisMapping, Integer> mem = new HashMap<>();

        return q -> q.entrySet().stream()
                .filter(actionFor(axisMapping, mem))
                .map(p -> TriggerPosition.builder()
                        .position(p.getValue())
                        .type(axisMapping)
                        .build())
                .filter(edgeValue)
                .map(p -> p.withModifiers(MODIFIER.getIntrospector().getModifiersResetEvents().stream()
                        .map(EButtonAxisMapping::getByMappingName)
                        .collect(Collectors.toSet())
                ))
                .forEach(digitizer.digitize());
    }

    Predicate<Map.Entry<String, Integer>> actionFor(EButtonAxisMapping name, Map<EButtonAxisMapping, Integer> mem) {
        return AxisMapper.valueFor(name.getMapping()).and(p -> {
            Integer prev = mem.put(name, p.getValue());

            if (prev == null) return false;

            int pos = p.getValue();

            return pos > 0 ? pos > prev : pos < prev;
        });
    }

    Map<EButtonAxisMapping, Set<EButtonAxisMapping>> triggerModifierMap = new HashMap<>();
    Set<EButtonAxisMapping> modifiers = new HashSet<>();

    public Flux<GamepadEvent> getButtonEventStream() {
        return qualifiedEventStream.asFlux()
                .filter(q -> {
                            Set<EButtonAxisMapping> m = Optional.ofNullable(q.getModifiers())
                                    .orElse(Set.of());

//                            Set<EButtonAxisMapping> mods = triggerModifierMap.remove(q.getType());


//                    System.out.println("modifiers: " + m);Set.of()

                            if (modifiers.isEmpty() && m.isEmpty()) return true;

                            q.getModifiers().addAll(modifiers);

                            boolean b = modifiers.isEmpty() ?
                                    modifiers.addAll(m) : !modifiers.remove(q.getType());

//                            triggerModifierMap.remove()

//                    System.out.println("result: " + b);

                            return b;
                        }
                )
                .publishOn(Schedulers.parallel())
                ;
    }
}
