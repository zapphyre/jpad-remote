package org.asmus.introspect.impl;

import lombok.SneakyThrows;
import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class ReleaseIntrospector extends BaseIntrospector {

    Set<String> modifiers = new HashSet<>();

    Predicate<ButtonClick> notModifier = q -> !modifiers.remove(q.getPush().getName());
    Predicate<ButtonClick> buttonWasPressedAndReleased = buttonWasPressed.and(buttonWasReleased);

    @SneakyThrows
    public Function<List<TimedValue>, ButtonClick> translate(List<String> forButtonNames) {
        return q -> q.stream()
                .filter(relevantButtonAction(forButtonNames))
                .map(pairWithPreviousValue)
                .filter(buttonStateChanged)
                .filter(buttonWasPressedAndReleased)
                .filter(notModifier)
                .reduce(lastElement)
                .map(c -> c.withModifiers(getModifiersResetEvents()))
                .orElse(null);
    }

    public Set<String> getModifiersResetEvents() {
        return holding.stream()
                .map(TimedValue::getName)
                .peek(modifiers::add) // adding holding button as a modifier will later reset them by `notModifier` predicate
                .collect(Collectors.toSet());
    }
}
