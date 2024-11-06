package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import org.asmus.model.Gamepad;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();
        GamepadIntrospector gamepadIntrospector = new GamepadIntrospector();

        Flux<Gamepad> gamepadFlux = joyWorker.hookOnJoy("/dev/input/js0");

        Disposable disposable = gamepadFlux
                .map(gamepadIntrospector::introspect)
                .filter(Predicate.not(List::isEmpty))
                .map(Object::toString)
                .subscribe(log::info);

        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));
    }
}
