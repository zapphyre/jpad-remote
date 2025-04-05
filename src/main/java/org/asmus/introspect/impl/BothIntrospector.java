package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.Set;
import java.util.stream.Collectors;

@Value
public class BothIntrospector extends BaseIntrospector {

    @Override
    public ButtonClick translate(ButtonClick buttonClick) {
        return buttonClick;
    }

//    @Override
//    public Set<String> getModifiersResetEvents() {
//        return holding.stream()
//                .map(TimedValue::getName)
//                .collect(Collectors.toSet());
//    }
}
