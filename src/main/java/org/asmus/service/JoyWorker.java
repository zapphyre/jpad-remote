package org.asmus.service;

import org.asmus.function.GamepadInputGroupQuery;
import org.asmus.model.Gamepad;
import org.asmus.evt.EAxisGamepadEvt;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.model.GamepadDefinition;
import org.asmus.model.GamepadStateStream;
import org.asmus.model.Reducible;
import org.bbi.linuxjoy.LinuxJoystick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class JoyWorker {

    private static final Logger log = LoggerFactory.getLogger(JoyWorker.class);
    private final Sinks.Many<Gamepad> buttonStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<Gamepad> axisStream = Sinks.many().multicast().directBestEffort();
    private Gamepad gamepad = Gamepad.builder().build();

    public GamepadStateStream hookOnDefault() {
        return hookOn(GamepadDefinition.builder().build());
    }

    BiFunction<Gamepad, EButtonGamepadEvt, Gamepad> reducer(GamepadInputGroupQuery<Boolean> buttonQuery) {
        return (gamepad, evt) -> buttonQuery.getValueForIndex(evt.getNum())
                .targetReturningSetter(evt.getSetter().setOn(gamepad));
    }

    <T> GamepadInputGroupQuery<T> setorAbout(Function<Integer, T> getter) {
        return idx -> witter -> witter.setTriggered(getter.apply(idx));
    }

    public GamepadStateStream hookOn(GamepadDefinition definition) {
        LinuxJoystick j = new LinuxJoystick(definition.getDev(), definition.getButtons(), definition.getAxis());

        GamepadInputGroupQuery<Boolean> buttonStateSetor = setorAbout(j::getButtonState);
        GamepadInputGroupQuery<Integer> axisStateSetor = setorAbout(j::getAxisState);

        j.open();

//        ScheduledFuture<?> emiterCloseable = Executors.newSingleThreadScheduledExecutor()
//                .scheduleAtFixedRate(() -> buttonStream.tryEmitNext(gamepad), 30, 20, TimeUnit.MILLISECONDS);

        ScheduledFuture<?> pollerCloseable = Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {
                    while (true) {

                        j.poll();

                        Gamepad gamepadBtn = Arrays.stream(EButtonGamepadEvt.values())
                                .reduce(gamepad, (q, p) -> p.getReducer(buttonStateSetor).apply(q, p), laterMerger);
                        buttonStream.tryEmitNext(gamepadBtn);

                        Gamepad gamepadAxs = Arrays.stream(EAxisGamepadEvt.values())
                                .reduce(gamepad, (q, p) -> p.getReducer(axisStateSetor).apply(q, p), laterMerger);
                        axisStream.tryEmitNext(gamepadAxs);

                    }
                }, 0, TimeUnit.MILLISECONDS);

        return GamepadStateStream.builder()
                .axisFlux(axisStream.asFlux())
                .buttonFlux(buttonStream.asFlux())
                .build();
    }

    BinaryOperator<Gamepad> laterMerger = (p, q) -> q;


}