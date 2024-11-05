package org.asmus.service;

import lombok.Getter;
import org.asmus.function.GamepadInputGroupQuery;
import org.asmus.yt.model.evt.EAxisGamepadEvt;
import org.asmus.yt.model.evt.EButtonGamepadEvt;
import org.asmus.yt.model.Gamepad;
import org.bbi.linuxjoy.LinuxJoystick;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class JoyWorker {

    private Gamepad gamepad = Gamepad.builder().build();
    private final Sinks.Many<Gamepad> sink = Sinks.many().multicast().directBestEffort();

    @Getter
    private final Flux<Gamepad> gamepads = sink.asFlux().distinctUntilChanged();

    BinaryOperator<Gamepad> laterMerger = (p, q) -> q;
    BiFunction<Gamepad, EButtonGamepadEvt, Gamepad> reducer(GamepadInputGroupQuery<Boolean> buttonQuery) {
        return (gamepad, evt) -> buttonQuery.getValueForComponent(evt.getNum())
                .targetReturningSetter(evt.getSetter().setOn(gamepad));
    }

    public void hookOnJoy(String dev) {
        LinuxJoystick j = new LinuxJoystick(dev, 11, 8);

        ForJoystick manager = new ForJoystick(gamepad);

        GamepadInputGroupQuery<Boolean> buttonStateSetor = manager.setorAbout(j::getButtonState);
        GamepadInputGroupQuery<Integer> axisStateSetor = manager.setorAbout(j::getAxisState);

        j.open();

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> sink.tryEmitNext(gamepad), 30, 20, TimeUnit.MILLISECONDS);

        while (true) {
            j.poll();

            gamepad = Arrays.stream(EButtonGamepadEvt.values())
                    .reduce(gamepad, (q, p) -> p.getReducer(buttonStateSetor).apply(q, p), laterMerger);

            gamepad = Arrays.stream(EAxisGamepadEvt.values())
                    .reduce(gamepad, (q, p) -> p.getReducer(axisStateSetor).apply(q, p), laterMerger);
        }
    }

    record ForJoystick(Gamepad gamepad) {

        public <T> GamepadInputGroupQuery<T> setorAbout(Function<Integer, T> getter) {
            return pos -> setter -> {
                setter.setTriggered(getter.apply(pos));
                return gamepad;
            };
        }
    }
}