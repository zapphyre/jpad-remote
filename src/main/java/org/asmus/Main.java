package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import org.asmus.model.Gamepad;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;

@Slf4j
public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();
        GamepadIntrospector gamepadIntrospector = new GamepadIntrospector();

        Flux<Gamepad> gamepadFlux = joyWorker.hookOnJoy("/dev/input/js0");
        gamepadFlux
                .subscribe(gamepadIntrospector::introspect);

        gamepadFlux
                .filter(Gamepad::isA)
                .contextWrite(q -> q)
                .subscribe(g -> log.info("now A"));
    }
}
