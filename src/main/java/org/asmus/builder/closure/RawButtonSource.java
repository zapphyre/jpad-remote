package org.asmus.builder.closure;

import org.asmus.model.GamepadEvent;
import org.asmus.model.TimedValue;
import reactor.core.publisher.Flux;

import java.util.List;

@FunctionalInterface
public interface RawButtonSource {

    Flux<GamepadEvent> device(List<TimedValue> buttonStates);
}
