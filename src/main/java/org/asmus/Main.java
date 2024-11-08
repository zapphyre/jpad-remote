package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.Gamepad;
import org.asmus.model.QualifiedEType;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();
        GamepadIntrospector gamepadIntrospector = new GamepadIntrospector();

        Flux<Gamepad> gamepadFlux = joyWorker.hookOnJoy("/dev/input/js0");

        gamepadFlux
                .mapNotNull(gamepadIntrospector::introspect)
                .filter(Predicate.not(List::isEmpty))
//                .delayElements(Duration.ofMillis(420))
//                .window(Duration.ofMillis(420))
//                .flatMap(Flux::collectList)  // Collecting items in each window into a list
//                .doOnNext(q -> log.info("click count: {}", q.size()))
                .map(EventMapper::translateTimed)
//                .bufferUntilChanged(QualifiedEType::getType)
//                .buffer(Duration.ofMillis(500)) // Collect events within each 420ms period
                .bufferTimeout(3, Duration.ofMillis(420))
                .flatMap(events -> events.size() == 1 ?
                        Flux.just(events.getFirst()) : Flux.just(events.getLast()))
                .map(Object::toString)
                .subscribe(log::info);

//        Disposable disposable = gamepadFlux
//                .map(gamepadIntrospector::introspect)
//                .sample(Duration.ofMillis(420))
//                .filter(Predicate.not(List::isEmpty))
//                .map(EventMapper::translate)
//                .map(Object::toString)
//                .subscribe(log::info);

//        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));
    }
}
