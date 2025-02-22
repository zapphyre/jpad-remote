package org.asmus.qualifier.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.model.ButtonClick;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.qualifier.Qualifier;
import org.asmus.qualifier.QualifyBuilder;
import reactor.core.publisher.Sinks;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class BaseQualifier implements QualifyBuilder, Qualifier {

    final long longStep = 420;

    Sinks.Many<GamepadEvent> qualifiedEventStream;

    @Override
    public Qualifier useStream(Sinks.Many<GamepadEvent> stream) {
        qualifiedEventStream = stream;
        return this::qualify;
    }

    GamepadEvent toGamepadEventWith(ButtonClick q) {
        return GamepadEvent.builder()
                .type(EButtonAxisMapping.getByName(q.getRelease().getName()))
                .longPress(computeIsLongPress(q))
                .modifiers(convertModifiers(q))
                .eventName(q.getRelease().getName())
                .build();
    }

    Set<EButtonAxisMapping> convertModifiers(ButtonClick click) {
        return click.getModifiers().stream()
                .map(EButtonAxisMapping::getByName)
                .collect(Collectors.toSet());
    }

    boolean computeIsLongPress(ButtonClick tvPair) {
        return tvPair.getRelease().getTime() - tvPair.getPush().getTime() > longStep;
    }
}
