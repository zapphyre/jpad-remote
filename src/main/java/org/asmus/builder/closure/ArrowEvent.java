package org.asmus.builder.closure;

import org.asmus.model.GamepadEvent;
import org.asmus.service.JoyWorker;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface ArrowEvent {

    Flux<GamepadEvent> device(JoyWorker worker);
}
