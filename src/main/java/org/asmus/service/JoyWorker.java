package org.asmus.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.evt.EAxisGamepadEvt;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.function.GamepadInputGroupQuery;
import org.asmus.model.Gamepad;
import org.asmus.model.GamepadDefinition;
import org.asmus.model.GamepadStateStream;
import org.bbi.linuxjoy.LinuxJoystick;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@Slf4j
public class JoyWorker {

    private final Sinks.Many<Gamepad> buttonStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<Gamepad> axisStream = Sinks.many().multicast().directBestEffort();
    private final Gamepad gamepad = Gamepad.builder().build();

    public GamepadStateStream hookOnDefault() {
        return hookOn(GamepadDefinition.builder().build());
    }

    @SneakyThrows
    public GamepadStateStream hookOn(GamepadDefinition definition) {
        LinuxJoystick j = new LinuxJoystick(definition.getDev(), definition.getButtons(), definition.getAxis());

        GamepadInputGroupQuery<Boolean> buttonStatusGamepad = gamepadWith(j::getButtonState);
        GamepadInputGroupQuery<Integer> axisStateGamepad = gamepadWith(j::getAxisState);

        j.open();

//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
        Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
            if (!j.isDeviceOpen() || !j.isChanged()) return;

            while (true) {
                j.poll();

                Gamepad gamepadBtn = Arrays.stream(EButtonGamepadEvt.values())
                        .reduce(gamepad, (q, p) -> p.accState(buttonStatusGamepad).apply(q, p), laterMerger);
                buttonStream.tryEmitNext(gamepadBtn);

                Gamepad gamepadAxs = Arrays.stream(EAxisGamepadEvt.values())
                        .reduce(gamepad, (q, p) -> p.accState(axisStateGamepad).apply(q, p), laterMerger);
                axisStream.tryEmitNext(gamepadAxs);
            }
//        }, 0, 50, TimeUnit.MILLISECONDS);
        });

//        ScheduledFuture<?> pollerCloseable = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//        Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
//        Thread.ofVirtual().start(() -> {
//            while (true) {
//
//
//            }
////        }, 0, 50, TimeUnit.MILLISECONDS);
//        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            pollerCloseable.cancel(true);
            axisStream.tryEmitComplete();
            buttonStream.tryEmitComplete();
            j.close();
        }));

        return GamepadStateStream.builder()
                .axisFlux(axisStream.asFlux())
                .buttonFlux(buttonStream.asFlux())
                .build();
    }

    <T> GamepadInputGroupQuery<T> gamepadWith(Function<Integer, T> getter) {
        return idx -> witter -> witter.setTriggered(getter.apply(idx));
    }

    BinaryOperator<Gamepad> laterMerger = (p, q) -> q;
}