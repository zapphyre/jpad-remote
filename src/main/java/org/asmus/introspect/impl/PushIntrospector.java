package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;

import java.util.Optional;
import java.util.function.Predicate;

@Value
public class PushIntrospector extends BaseIntrospector {

    @Override
    public ButtonClick translate(ButtonClick buttonClick) {
        return Optional.of(buttonClick)
                .filter(buttonWasPressed)
                .filter(Predicate.not(buttonWasReleased))
                .orElse(null);
    }
}
