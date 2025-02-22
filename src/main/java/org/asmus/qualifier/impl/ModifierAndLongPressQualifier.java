package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;

public class ModifierAndLongPressQualifier extends BaseQualifier {

    // this needs to know multiplicity too
    public void qualify(ButtonClick click) {
        qualifiedEventStream.tryEmitNext(toGamepadEventWith(click));
    }
}
