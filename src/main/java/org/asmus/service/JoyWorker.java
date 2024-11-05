package org.asmus.service;

import lombok.Getter;
import org.asmus.yt.model.EAxisGamepadEvt;
import org.asmus.yt.model.EButtonGamepadEvt;
import org.asmus.yt.model.Gamepad;
import org.asmus.yt.model.Reducable;
import org.bbi.linuxjoy.LinuxJoystick;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class JoyWorker {

    private Gamepad gamepad = Gamepad.builder().build();
    private final Sinks.Many<Gamepad> sink = Sinks.many().multicast().directBestEffort();

    @Getter
    private final Flux<Gamepad> gamepads = sink.asFlux();

    BinaryOperator<Gamepad> laterMerger = (p, q) -> q;
    BiFunction<Gamepad, EButtonGamepadEvt, Gamepad> reducer(Function<Integer, Function<Consumer<Boolean>, Gamepad>> buttonQuery) {
        return (gamepad, evt) -> buttonQuery.apply(evt.getNum()).apply(evt.getSetter().apply(gamepad));
    }

    public void hookOnJoy(String dev) {
        LinuxJoystick j = new LinuxJoystick(dev, 11, 8);

        ForJoystick manager = new ForJoystick(gamepad);

        var queryButton = manager.query(j::getButtonState);
        var queryAxis = manager.query(j::getAxisState);

        j.open();

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> sink.tryEmitNext(gamepad), 30, 20, TimeUnit.MILLISECONDS);

        while (true) {
            j.poll();

            gamepad = Arrays.stream(EButtonGamepadEvt.values())
                    .reduce(gamepad, (q, p) -> p.getReducer(queryButton).apply(q, p), laterMerger);

            gamepad = Arrays.stream(EAxisGamepadEvt.values())
                    .reduce(gamepad, (q, p) -> p.getReducer(queryAxis).apply(q, p), laterMerger);
        }
    }

    record ForJoystick(Gamepad gamepad) {

        public <T> Function<Integer, Function<Consumer<T>, Gamepad>> query(Function<Integer, T> getter) {
            return pos -> setter -> {
                setter.accept(getter.apply(pos));
                return gamepad;
            };
        }
    }
}