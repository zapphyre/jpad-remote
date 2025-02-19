package org.asmus.builder;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.closure.ArrowEvent;
import org.asmus.builder.closure.OsDevice;
import org.asmus.builder.closure.RawEventListener;
import org.asmus.introspect.impl.BothIntrospector;
import org.asmus.introspect.impl.PushIntrospector;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.NamingConstants;
import org.asmus.qualifier.impl.*;
import org.asmus.service.JoyWorker;
import org.asmus.tool.AxisMapper;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GamepadEventSourceBuilder {

    static Predicate<Map<String, Integer>> notZeroFor(String axisName) {
        return q -> q.get(axisName) != 0;
    }

    public static final ActuationBehaviour MODIFIER = ActuationBehaviour.builder()
            .introspector(new ReleaseIntrospector())
            .qualifier(new ModifierAndLongPressQualifier())
            .build();

    public static final ActuationBehaviour LONG = ActuationBehaviour.builder()
            .introspector(new BothIntrospector())
            .qualifier(new AutoLongClickQualifier())
            .build();

    public static final ActuationBehaviour PUSH = ActuationBehaviour.builder()
            .introspector(new PushIntrospector())
            .qualifier(new ImmediateQualifier())
            .build();

    public static final ActuationBehaviour MULTIPLICITY = ActuationBehaviour.builder()
            .introspector(new BothIntrospector())
            .qualifier(new MultiplicityQualifier())
            .build();

    static List<String> names(List<EButtonAxisMapping> buttons) {
        return buttons.stream().map(EButtonAxisMapping::getInternal).toList();
    }

    public static RawEventListener rawEvents() {
        return JoyWorker::getButtonStream;
    }

    public static OsDevice getButtonStream() {
        return worker -> behaviour -> buttons -> {
            worker.getButtonStream()
                    .mapNotNull(behaviour.getIntrospector().translate(names(buttons)))
                    .subscribe(behaviour.getQualifier()::qualify);

            return behaviour.getQualifier().getQualifiedEventStream().asFlux()
                    .publish().autoConnect();
        };
    }

    public static ArrowEvent getArrowsStream() {
        return worker -> {
            ReleaseIntrospector introspector = new ReleaseIntrospector();
            ActuationBehaviour act = ActuationBehaviour.builder()
                    .introspector(introspector)
                    .build();

            Flux<GamepadEvent> buttons = getButtonStream()
                    .device(worker)
                    .actuation(act)
                    .buttons(Arrays.asList(EButtonAxisMapping.values()));

            Flux<GamepadEvent> vertical = worker.getAxisStream()
                    .filter(notZeroFor(NamingConstants.ARROW_Y))
                    .map(AxisMapper.mapVertical);

            Flux<GamepadEvent> horizontal = worker.getAxisStream()
                    .filter(notZeroFor(NamingConstants.ARROW_X))
                    .map(AxisMapper.mapHorizontal);

            return Flux.merge(vertical, horizontal)
                    .map(q -> q.withModifiers(introspector.getModifiersResetEvents().stream()
                            .map(EButtonAxisMapping::getByName)
                            .collect(Collectors.toSet())
                    ))
                    .doOnCancel(buttons::subscribe)
                    .publish().autoConnect();
        };
    }
}
