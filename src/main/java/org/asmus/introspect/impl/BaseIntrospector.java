package org.asmus.introspect.impl;

import org.asmus.introspect.Introspector;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public abstract class BaseIntrospector implements Introspector {

    final Set<TimedValue> holding = new LinkedHashSet<>();

    final Predicate<ButtonClick> buttonWasPressed = q -> holding.add(q.getPush());
    final Predicate<ButtonClick> buttonWasReleased = q -> holding.remove(q.getRelease()) && holding.remove(q.getPush());

    Set<String> modifiers = new HashSet<>();

    Predicate<ButtonClick> notModifier = q -> !modifiers.remove(q.getPush().getName());

    final Predicate<TimedValue> relevantButtonAction(List<String> forButtonNames) {
        return q -> forButtonNames.contains(q.getName());
    }

    BinaryOperator<ButtonClick> lastElement = (p, q) -> q;
}
