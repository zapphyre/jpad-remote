package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GamepadDefinition {
    @Builder.Default
    String dev = "/dev/input/js0";
    int buttons = 11;
    int axis = 8;
}
