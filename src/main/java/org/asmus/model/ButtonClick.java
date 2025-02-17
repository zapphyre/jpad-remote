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
    @Builder.Default
    Set<String> modifiers = Set.of();

    TimedValue push;
    TimedValue release;
}
