package org.asmus.tool;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GamepadIntrospector {

    Map<String, TimedValue> values = new HashMap<>();
    Set<TimedValue> holding = new LinkedHashSet<>();
    Set<String> modifiers = new HashSet<>();

    BinaryOperator<ButtonClick> lastElement = (p, q) -> q;

    Predicate<ButtonClick> buttonStateChanged = q -> q.getPush().isValue() != q.getRelease().isValue();
    Predicate<ButtonClick> notModifier = q -> !modifiers.remove(q.getPush().getName());
    Predicate<ButtonClick> buttonWasPressed = q -> holding.add(q.getPush());
    Predicate<ButtonClick> buttonWasReleased = q -> holding.remove(q.getRelease()) && holding.remove(q.getPush());
    Predicate<ButtonClick> buttonWasPressedAndReleased = buttonWasPressed.and(buttonWasReleased);

    Function<TimedValue, ButtonClick> pairWithPreviousValue = current -> {
        TimedValue previous = values.computeIfAbsent(current.getName(), TimedValue::new);

        if (!current.equals(previous))
            values.put(current.getName(), current);

        return ButtonClick.builder()
                .push(previous)
                .release(current)
                .build();
    };

    @SneakyThrows
    public ButtonClick releaseEvent(List<TimedValue> values) {
        return values.stream()
                .map(pairWithPreviousValue)
                .filter(buttonStateChanged)
                .filter(buttonWasPressedAndReleased)
                .filter(notModifier)
                .reduce(lastElement)
                .map(q -> q.withModifiers(getModifiersResetEvents()))
                .orElse(null);
    }

    public Set<String> getModifiersResetEvents() {
        return holding.stream()
                .map(TimedValue::getName)
                .peek(modifiers::add)
                .collect(Collectors.toSet());
    }

}
