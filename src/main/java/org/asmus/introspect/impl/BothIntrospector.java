package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.List;

@Value
public class BothIntrospector extends BaseIntrospector{

    @Override
    public ButtonClick translate(List<TimedValue> states) {
        return states.stream()
                .map(pairWithPreviousValue)
                .filter(buttonStateChanged)
//                .peek(q -> System.out.println("ButtonStateChanged: " + q))
                .reduce(lastElement)
                .orElse(null);
    }
}
