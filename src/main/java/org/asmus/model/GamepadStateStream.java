package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Flux;

import java.util.Map;

@Value
@Builder
public class GamepadStateStream {
    Flux<Map<String, Boolean>> buttonFlux;
    Flux<Map<String, Integer>> axisFlux;
}
