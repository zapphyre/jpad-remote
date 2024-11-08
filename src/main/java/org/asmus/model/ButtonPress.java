package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import org.asmus.tool.GamepadIntrospector;

@Value
@Builder
public class ButtonPress {
    GamepadIntrospector.TVPair starting;
    GamepadIntrospector.TVPair ending;
}
