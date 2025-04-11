package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.Set;
import java.util.stream.Collectors;

@Value
public class BothIntrospector extends BaseIntrospector {

    @Override
    // all predicates need to be called for state lists to converge to correct state
    public ButtonClick translate(ButtonClick buttonClick) {
        boolean press = buttonWasPressed.test(buttonClick);
        boolean release = buttonWasReleased.test(buttonClick);

        boolean notMod = notModifier.test(buttonClick);

        if (release)
            return buttonClick.withModifiers(holding.stream().map(TimedValue::getName).collect(Collectors.toSet()));

        return buttonClick;
    }
}
