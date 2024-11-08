package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@With
@Value
@Builder
public class MeteredKeyEvent {
    @Builder.Default
    LocalDateTime lastEvent = LocalDateTime.now();
    @Builder.Default
    AtomicInteger multiplicity = new AtomicInteger();

    public AtomicInteger incrementAndGetMultiplicity() {
        multiplicity.getAndIncrement();
        return multiplicity;
    }
}
