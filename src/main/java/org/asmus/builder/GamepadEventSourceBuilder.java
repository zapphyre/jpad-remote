package org.asmus.builder;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.closure.ArrowEvent;
import org.asmus.builder.closure.OsDevice;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.NamingConstants;
import org.asmus.tool.AxisMapper;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GamepadEventSourceBuilder {

    static Predicate<Map<String, Integer>> notZeroFor(String axisName) {
        return q -> q.get(axisName) != 0;
    }

    public OsDevice getButtonStream() {
        return worker -> behaviour -> buttons -> {
            worker.getButtonStream()
                    .mapNotNull(behaviour.getIntrospector().translate(buttons.stream().map(EButtonAxisMapping::name).toList()))
                    .subscribe(behaviour.getGetQualifier()::qualify);

            return behaviour.getOut().asFlux().publish().autoConnect();
        };
    }

    public ArrowEvent getArrowsStream() {
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
