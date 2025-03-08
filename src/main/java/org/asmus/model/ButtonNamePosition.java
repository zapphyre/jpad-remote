package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class ButtonNamePosition {
    boolean axis;
    boolean hat;
    String buttonName;
    int position;
    String buttonCode;
}
