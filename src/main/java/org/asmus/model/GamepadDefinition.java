package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GamepadDefinition {
    String dev = "/dev/input/js0";
    @Builder.Default
    int buttons = 11;
    int axis = 8;
}
