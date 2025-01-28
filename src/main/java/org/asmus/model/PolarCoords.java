package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class PolarCoords {
    EType type;
    double radius;
    double theta;
}
