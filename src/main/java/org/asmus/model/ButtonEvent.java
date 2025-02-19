package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ButtonEvent {
    @EqualsAndHashCode.Include
    String name;
    boolean release;
}
