package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;

public class ImmediateQualifier extends BaseQualifier {

    @Override
    public void qualify(ButtonClick click) {

        qualifiedEventStream.tryEmitNext(toGamepadEventWith(click).withLongPress(false));
    }
}
