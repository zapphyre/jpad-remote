package org.asmus.facade;

import org.asmus.model.QualifiedEType;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

public class TimedButtonGamepadFactory {

    public static Flux<QualifiedEType> createGamepadEventStream() {
        return new JoyWorker().hookOnDefault()
                .mapNotNull(GamepadIntrospector::introspect)
                .filter(Predicate.not(List::isEmpty))
                .map(EventMapper::translateTimed)
                .bufferTimeout(3, Duration.ofMillis(300))
                .flatMap(events -> events.size() == 1 ?
                        Flux.just(events.getFirst()) : Flux.just(events.getLast()));
    }
}
