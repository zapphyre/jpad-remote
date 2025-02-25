package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;
import org.asmus.model.EQualificationType;

public class ImmediateQualifier extends BaseQualifier {

    @Override
    public void qualify(ButtonClick click) {
        qualifiedEventStream.tryEmitNext(toGamepadEventWith(click).withQualified(EQualificationType.PUSH).withLongPress(false));
    }
}
