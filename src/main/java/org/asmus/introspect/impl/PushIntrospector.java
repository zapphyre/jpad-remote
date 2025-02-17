package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.List;
import java.util.function.Predicate;

@Value
public class PushIntrospector extends BaseIntrospector {

    @Override
    public ButtonClick translate(List<TimedValue> states) {
        return states.stream()
                .map(pairWithPreviousValue)
                .filter(buttonStateChanged)
                .filter(buttonWasPressed)
                .filter(Predicate.not(buttonWasReleased))
                .reduce(lastElement)
                .orElse(null);
    }
}
