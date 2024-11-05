package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GEvent {
    String name;
    EType type;
}
