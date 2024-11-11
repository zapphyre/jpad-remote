package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Flux;

@Value
@Builder
public class GamepadStateStream {
    Flux<Gamepad> buttonFlux;
    Flux<Gamepad> axisFlux;
}
