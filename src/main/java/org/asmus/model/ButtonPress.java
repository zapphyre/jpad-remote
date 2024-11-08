package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ButtonPress {
    TVPair starting;
    TVPair ending;
}
