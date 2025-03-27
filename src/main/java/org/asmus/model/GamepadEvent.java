package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

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
    Set<EButtonAxisMapping> modifiers;
    ELogicalEventType logicalEventType;

    @Builder.Default
    EQualificationType qualified = EQualificationType.MULTIPLE;
}
