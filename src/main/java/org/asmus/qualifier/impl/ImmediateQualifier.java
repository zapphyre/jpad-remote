package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;

public class ImmediateQualifier extends BaseQualifier {

    public ImmediateQualifier(Sinks.Many<GamepadEvent> output) {
        super(output);
    }

    @Override
    public void qualify(ButtonClick click) {
        output.tryEmitNext(toGamepadEventWith(click).withLongPress(false));
    }
}
