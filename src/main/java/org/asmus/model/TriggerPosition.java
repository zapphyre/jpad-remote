package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Set;

@With
@Value
@Builder
public class TriggerPosition {
    int position;
    EButtonAxisMapping type;
    Set<EButtonAxisMapping> modifiers;
}
