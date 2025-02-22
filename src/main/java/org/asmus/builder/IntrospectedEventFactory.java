package org.asmus.builder;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.closure.button.FilteredBehaviour;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.introspect.Introspector;
import org.asmus.introspect.impl.BothIntrospector;
import org.asmus.introspect.impl.PushIntrospector;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.mapper.GamepadStateMapper;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.NamingConstants;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IntrospectedEventFactory {
    private final Sinks.Many<GamepadEvent> qualifiedEventStream = Sinks.many().multicast().directBestEffort();
    private Introspector momentaryIntrospector = new BothIntrospector();

    static Predicate<Map.Entry<String, Integer>> notZeroFor(String axisName) {
        return q -> q.getKey().equals(axisName) && q.getValue() != 0;
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

    public FilteredBehaviour getButtonStream() {
        GamepadStateMapper gamepadStateMapper = new GamepadStateMapper();
        return behaviour -> states -> states.stream()
                .map(gamepadStateMapper::map)
                .filter(Objects::nonNull)
                .map(q -> (momentaryIntrospector = behaviour.apply(q).getIntrospector()).translate(q))
                .filter(Objects::nonNull)
                .forEach(q -> behaviour.apply(q).getQualifier().useStream(qualifiedEventStream).qualify(q));
    }

    public RawArrowSource getArrowsStream() {
        GamepadStateMapper gamepadStateMapper = new GamepadStateMapper();

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
                    .map(q -> q.withModifiers(
                            momentaryIntrospector.getModifiersResetEvents().stream()
                            .map(EButtonAxisMapping::getByName)
                            .collect(Collectors.toSet())
                    ))
                    .subscribe(qualifiedEventStream::tryEmitNext);
        };
    }

    public Flux<GamepadEvent> getEventStream() {
        return qualifiedEventStream.asFlux();
    }
}
