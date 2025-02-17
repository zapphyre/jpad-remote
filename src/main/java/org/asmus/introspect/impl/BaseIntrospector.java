package org.asmus.introspect.impl;

import org.asmus.introspect.Introspector;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BaseIntrospector implements Introspector {

    final Map<String, TimedValue> values = new HashMap<>();
    final Set<TimedValue> holding = new LinkedHashSet<>();

    final Predicate<ButtonClick> buttonStateChanged = q -> q.getPush().isValue() != q.getRelease().isValue();
    final Predicate<ButtonClick> buttonWasPressed = q -> holding.add(q.getPush());
    final Predicate<ButtonClick> buttonWasReleased = q -> holding.remove(q.getRelease()) && holding.remove(q.getPush());

    final Predicate<TimedValue> relevantButtonAction(List<String> forButtonNames) {
        return q -> forButtonNames.contains(q.getName());
    }

    BinaryOperator<ButtonClick> lastElement = (p, q) -> q;

    Function<TimedValue, ButtonClick> pairWithPreviousValue = current -> {
        TimedValue previous = values.computeIfAbsent(current.getName(), TimedValue::new);

        if (!current.equals(previous))
            values.put(current.getName(), current);

        return ButtonClick.builder()
                .push(previous)
                .release(current)
                .build();
    };
}
