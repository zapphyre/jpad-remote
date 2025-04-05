package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.HashSet;
import java.util.Set;

@With
@Value
@Builder
public class GamepadEvent {

    EButtonAxisMapping type;
    String eventName;
    @Builder.Default
    EMultiplicity multiplicity = EMultiplicity.CLICK;
    boolean longPress;
    @Builder.Default
    Set<EButtonAxisMapping> modifiers = new HashSet<>();
    ELogicalEventType logicalEventType;

    @Builder.Default
    EQualificationType qualified = EQualificationType.MULTIPLE;
}
