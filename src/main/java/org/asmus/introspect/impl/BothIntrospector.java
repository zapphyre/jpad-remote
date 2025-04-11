package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.stream.Collectors;

@Value
public class BothIntrospector extends BaseIntrospector {

    @Override
    public ButtonClick translate(ButtonClick buttonClick) {
        boolean release = buttonWasReleased.test(buttonClick);

        return release ?
                buttonClick.withModifiers(holding.stream().map(TimedValue::getName).collect(Collectors.toSet())) :
                buttonClick;
    }
}
