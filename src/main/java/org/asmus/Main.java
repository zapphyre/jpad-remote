package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();

        Disposable disposable = joyWorker.hookOnJoy("/dev/input/js0")
                .mapNotNull(GamepadIntrospector::introspect)
                .filter(Predicate.not(List::isEmpty))
                .map(EventMapper::translateTimed)
                .bufferTimeout(3, Duration.ofMillis(300))
                .flatMap(events -> events.size() == 1 ?
                        Flux.just(events.getFirst()) : Flux.just(events.getLast()))
                .map(Object::toString)
                .subscribe(log::info);

        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));
    }
}
