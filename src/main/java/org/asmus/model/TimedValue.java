package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@EqualsAndHashCode
public class TimedValue {
    @Builder.Default
    @EqualsAndHashCode.Exclude
    long time = System.currentTimeMillis();

    String name;
    boolean value;
}
