package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import java.util.Set;

@With
@Value
@Builder
@EqualsAndHashCode
public class ButtonClick {
    Set<String> modifiers;

    TimedValue push;
    TimedValue release;
}
