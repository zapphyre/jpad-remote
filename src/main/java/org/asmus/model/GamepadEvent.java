package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

@With
@Value
@Builder
public class GamepadEvent {
    EButtonAxisMapping type;
    String eventName;
    EMultiplicity multiplicity;
    boolean longPress;
    List<EButtonAxisMapping> modifiers;
}
