package org.asmus.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class DigitizedTriggerEvt {
    ETriggerDigiEvt type;
    Set<EButtonAxisMapping> modifiers;
}
