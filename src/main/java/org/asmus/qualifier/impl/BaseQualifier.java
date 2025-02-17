package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.qualifier.Qualifier;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseQualifier implements Qualifier {

    long longStep = 210;

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
