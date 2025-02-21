package org.asmus.introspect.impl;

import lombok.Value;
import org.asmus.model.ButtonClick;

@Value
public class BothIntrospector extends BaseIntrospector {

    @Override
    public ButtonClick translate(ButtonClick buttonClick) {
        return buttonClick;
    }
}
