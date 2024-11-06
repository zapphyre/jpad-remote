package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import org.asmus.model.Gamepad;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Slf4j
public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();
        GamepadIntrospector gamepadIntrospector = new GamepadIntrospector();

        Flux<Gamepad> gamepadFlux = joyWorker.hookOnJoy("/dev/input/js0");

        Disposable disposable = gamepadFlux
                .subscribe(gamepadIntrospector::introspect);

        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));

        gamepadFlux
                .filter(Gamepad::isA)
                .contextWrite(q -> q)
                .subscribe(g -> log.info("now A"));
    }
}
