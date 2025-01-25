package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StickMovement {
    @Builder.Default
    PolarCoords left = PolarCoords.builder().build();
    @Builder.Default
    PolarCoords right = PolarCoords.builder().build();
}
