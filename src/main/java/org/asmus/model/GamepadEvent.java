package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Set;

@With
@Value
@Builder
public class GamepadEvent {

    @EqualsAndHashCode.Include
    EButtonAxisMapping type;
    String eventName;
    @Builder.Default
    EMultiplicity multiplicity = EMultiplicity.CLICK;
    boolean longPress;
    Set<EButtonAxisMapping> modifiers;
}
