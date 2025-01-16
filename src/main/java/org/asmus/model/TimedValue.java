package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TimedValue {
    
    @Builder.Default
    LocalDateTime date = LocalDateTime.now();
    @EqualsAndHashCode.Include
    String name;
    @EqualsAndHashCode.Include
    String value;
}
