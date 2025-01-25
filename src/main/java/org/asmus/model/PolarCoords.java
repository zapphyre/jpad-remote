package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PolarCoords {
    double radius;
    double theta;
}
