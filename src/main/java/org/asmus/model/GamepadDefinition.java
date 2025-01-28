package org.asmus.model;

import lombok.Builder;
import lombok.Value;

import java.nio.file.Path;

@Value
@Builder
public class GamepadDefinition {

    @Builder.Default
    Path dev = Path.of("/", "dev", "input", "js0");

    int buttons = 15;
    int axis = 8;
}
