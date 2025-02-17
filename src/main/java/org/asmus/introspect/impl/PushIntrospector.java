package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Value
public class PushIntrospector extends BaseIntrospector {

    @Override
    public Function<List<TimedValue>, ButtonClick> translate(List<String> forButtonNames) {
        return q -> q.stream()
                .filter(relevantButtonAction(forButtonNames))
                .map(pairWithPreviousValue)
                .filter(buttonStateChanged)
                .filter(buttonWasPressed)
                .filter(Predicate.not(buttonWasReleased))
                .reduce(lastElement)
                .orElse(null);
    }
}
