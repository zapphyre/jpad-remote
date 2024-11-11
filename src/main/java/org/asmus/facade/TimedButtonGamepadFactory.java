package org.asmus.facade;

import org.asmus.model.EType;
import org.asmus.model.GamepadStateStream;
import org.asmus.model.QualifiedEType;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

public class TimedButtonGamepadFactory {

    static Predicate<QualifiedEType> notFizzy = q -> !q.getType().equals(EType.FIZZY);

    public static Flux<QualifiedEType> createGamepadEventStream() {
        final GamepadStateStream stateStream = new JoyWorker().hookOnDefault();

        Flux<QualifiedEType> buttonStream = stateStream.getButtonFlux()
                .mapNotNull(GamepadIntrospector::introspect)
                .filter(Predicate.not(List::isEmpty))
                .map(EventMapper::translateTimed)
                .filter(notFizzy)
                .bufferTimeout(3, Duration.ofMillis(300))
                .flatMap(events -> events.size() == 1 ?
                        Flux.just(events.getFirst()) : Flux.just(events.getLast()));

        final Sinks.Many<QualifiedEType> out = Sinks.many().multicast().directBestEffort();
        buttonStream.subscribe(out::tryEmitNext);

        stateStream.getAxisFlux()
                .distinctUntilChanged()
                .map(EventMapper::translateAxis)
                .filter(notFizzy)
                .distinctUntilChanged()
                .subscribe(out::tryEmitNext);

        return out.asFlux();
//        return Flux.merge(buttonStream, stateStream.getAxisFlux().map(EventMapper::translateAxis));
//                .filter(q -> !q.getType().equals(EType.FIZZY)));
//        return stateStream.getAxisFlux().map(EventMapper::translateAxis).concatWith(buttonStream);
//        return Flux.concat(buttonStream, stateStream.getAxisFlux().map(EventMapper::translateAxis));
    }
}
