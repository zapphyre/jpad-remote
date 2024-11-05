package org.asmus.service;

import lombok.Getter;
import org.asmus.function.GamepadInputGroupQuery;
import org.asmus.yt.model.Gamepad;
import org.asmus.yt.model.evt.EAxisGamepadEvt;
import org.asmus.yt.model.evt.EButtonGamepadEvt;
import org.bbi.linuxjoy.LinuxJoystick;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class JoyWorker {

    private final Sinks.Many<Gamepad> sink = Sinks.many().multicast().directBestEffort();
    private Gamepad gamepad = Gamepad.builder().build();

    public Flux<Gamepad> hookOnJoy(String dev) {
        LinuxJoystick j = new LinuxJoystick(dev, 11, 8);

        GamepadInputGroupQuery<Boolean> buttonStateSetor = setorAbout(j::getButtonState);
        GamepadInputGroupQuery<Integer> axisStateSetor = setorAbout(j::getAxisState);

        j.open();

        ScheduledFuture<?> emiterCloseable = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> sink.tryEmitNext(gamepad), 30, 20, TimeUnit.MILLISECONDS);

        ScheduledFuture<?> pollerCloseable = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    j.poll();

                    gamepad = Arrays.stream(EButtonGamepadEvt.values())
                            .reduce(gamepad, (q, p) -> p.getReducer(buttonStateSetor).apply(q, p), laterMerger);

                    gamepad = Arrays.stream(EAxisGamepadEvt.values())
                            .reduce(gamepad, (q, p) -> p.getReducer(axisStateSetor).apply(q, p), laterMerger);
                }, 0, 20, TimeUnit.MILLISECONDS);

        return sink.asFlux()
                .distinctUntilChanged()
                .doOnTerminate(() -> {
                    j.close();
                    pollerCloseable.cancel(true);
                    emiterCloseable.cancel(true);
                });
    }

    BinaryOperator<Gamepad> laterMerger = (p, q) -> q;

    BiFunction<Gamepad, EButtonGamepadEvt, Gamepad> reducer(GamepadInputGroupQuery<Boolean> buttonQuery) {
        return (gamepad, evt) -> buttonQuery.getValueForIndex(evt.getNum())
                .targetReturningSetter(evt.getSetter().setOn(gamepad));
    }

    <T> GamepadInputGroupQuery<T> setorAbout(Function<Integer, T> getter) {
        return pos -> setter -> setter.setTriggered(getter.apply(pos));
    }
}