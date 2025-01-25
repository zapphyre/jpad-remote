package org.asmus.facade;

import org.asmus.component.EventQualificator;
import org.asmus.model.*;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

public class TimedButtonGamepadFactory {

    final static Predicate<QualifiedEType> notFizzy = q -> !q.getType().equals(EType.FIZZY);
    final static GamepadStateStream stateStream = new JoyWorker().hookOnDefault();

    public static Flux<QualifiedEType> getButtonStream() {
        Sinks.Many<QualifiedEType> out = Sinks.many().multicast().directBestEffort();
        EventQualificator eventQualificator = new EventQualificator(out);

        stateStream.getButtonFlux()
                .mapNotNull(GamepadIntrospector::introspect)
                .subscribe(eventQualificator::qualify);

        return out.asFlux();
    }

    public static Flux<StickMovement> getStickStream() {
        return stateStream.getAxisFlux()
                .map(EventMapper::translateAxis)
                ;
    }
}
