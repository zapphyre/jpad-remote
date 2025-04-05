package org.asmus.introspect.impl;

import lombok.SneakyThrows;
import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class ReleaseIntrospector extends BaseIntrospector {


    Predicate<ButtonClick> buttonWasPressedAndReleased = buttonWasPressed.and(buttonWasReleased);

    @SneakyThrows
    public ButtonClick translate(ButtonClick buttonClick) {
        return Optional.of(buttonClick)
                .filter(buttonWasPressedAndReleased)
                .filter(notModifier)
                .map(c -> c.withModifiers(getModifiersResetEvents()))
                .orElse(null);
    }

    public Set<String> getModifiersResetEvents() {
        return holding.stream()
                .map(TimedValue::getName)
                .peek(modifiers::add) // adding holding button as a modifier will later reset them by `notModifier` predicate
                .collect(Collectors.toSet());
    }

    public Set<String> getModifiers() {
        return modifiers;
    }
}
