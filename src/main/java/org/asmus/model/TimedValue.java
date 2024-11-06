package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class TimedValue {
    @Builder.Default
    LocalDateTime date = LocalDateTime.now();
    @EqualsAndHashCode.Include
    String name;
    String value;
}
