package org.asmus.builder;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.introspect.impl.BothIntrospector;
import org.asmus.introspect.impl.PushIntrospector;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.mapper.GamepadStateMapper;
import org.asmus.model.*;
import org.asmus.qualifier.impl.AutoLongClickQualifier;
import org.asmus.qualifier.impl.ImmediateQualifier;
import org.asmus.qualifier.impl.ModifierAndLongPressQualifier;
import org.asmus.qualifier.impl.MultiplicityQualifier;
import org.asmus.tool.AxisMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    Consumer<ButtonClick> qualify = c -> behaviours.forEach(q -> {
        Optional.ofNullable(c)
                .map(q.getIntrospector()::translate)
                .ifPresent(q.getQualifier().useStream(qualifiedEventStream)::qualify);
    });

    public OsDevice getButtonStream() {
        GamepadStateMapper gamepadStateMapper = new GamepadStateMapper();
        return states -> states.stream()
                .map(gamepadStateMapper::map)
                .forEach(qualify);
    }

    public RawArrowSource getArrowsStream() {
        return axisStates -> {
            List<GamepadEvent> vertical = axisStates.entrySet().stream()
                    .filter(notZeroFor(NamingConstants.ARROW_Y))
                    .map(AxisMapper.mapVertical)
                    .toList();

            List<GamepadEvent> horizontal = axisStates.entrySet().stream()
                    .filter(notZeroFor(NamingConstants.ARROW_X))
                    .map(AxisMapper.mapHorizontal)
                    .toList();

            Flux.merge(Flux.fromIterable(vertical), Flux.fromIterable(horizontal))
                    .map(q -> q.withQualified(EQualificationType.ARROW))
                    .map(q -> q.withModifiers(
                            MODIFIER.getIntrospector().getModifiersResetEvents().stream()
                            .map(EButtonAxisMapping::getByName)
                            .collect(Collectors.toSet())
                    ))
                    .subscribe(qualifiedEventStream::tryEmitNext);
        };
    }

    public Flux<GamepadEvent> getButtonEventStream() {
        return qualifiedEventStream.asFlux();
    }
}
