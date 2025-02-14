package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Set;

@With
@Value
@Builder
public class GamepadEvent {
    EButtonAxisMapping type;
    String eventName;
    EMultiplicity multiplicity;
    boolean longPress;
    Set<EButtonAxisMapping> modifiers;
}
