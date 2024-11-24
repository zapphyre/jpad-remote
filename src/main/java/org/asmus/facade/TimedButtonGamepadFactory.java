package org.asmus.facade;

import org.asmus.model.EType;
import org.asmus.model.GamepadStateStream;
import org.asmus.model.QualifiedEType;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

public class TimedButtonGamepadFactory {

    static Predicate<QualifiedEType> notFizzy = q -> !q.getType().equals(EType.FIZZY);

    public static Flux<QualifiedEType> createGamepadEventStream() {
        final GamepadStateStream stateStream = new JoyWorker().hookOnDefault();

        final Flux<QualifiedEType> buttonStream = stateStream.getButtonFlux()
                .mapNotNull(GamepadIntrospector::introspect)
                .filter(Predicate.not(List::isEmpty))
                .map(EventMapper::translateButtonTimed)
                .filter(notFizzy)
                .bufferTimeout(3, Duration.ofMillis(300))
                .flatMap(events -> events.size() == 1 ?
                        Flux.just(events.getFirst()) : Flux.just(events.getLast()));

        final Flux<QualifiedEType> axisStream = stateStream.getAxisFlux()
                .distinctUntilChanged()
                .map(EventMapper::translateAxis)
                .filter(notFizzy)
                .distinctUntilChanged();

        return Flux.merge(buttonStream, axisStream);
    }
}
