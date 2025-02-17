package org.asmus.builder.closure;

import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Flux;

import java.util.List;

@FunctionalInterface
public interface ButtonGroup {

    Flux<GamepadEvent> buttons(List<EButtonAxisMapping> buttons);
}
