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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static fs.watcher.FsWatcher.watch;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

@Slf4j
public class JoyWorker {

    private final Sinks.Many<Gamepad> buttonStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<Gamepad> axisStream = Sinks.many().multicast().directBestEffort();
    private final Gamepad gamepad = Gamepad.builder().build();
    private ScheduledFuture<?> pollerCloseable;

    @SneakyThrows
    public GamepadStateStream hookOnDefault() {
        return managedHooker(GamepadDefinition.builder().build());
    }

    @SneakyThrows
    public GamepadStateStream managedHooker(GamepadDefinition definition) {
        LinuxJoystick j = new LinuxJoystick(definition.getDev(), definition.getButtons(), definition.getAxis());
        Path gamepadPath = Path.of(definition.getDev());

        if (Files.exists(gamepadPath))
            processEvents(j);

        watch(gamepadPath)
                .forEvents(ENTRY_CREATE, ENTRY_DELETE)
                .onChange(q -> {
                    if (q.kind() == ENTRY_CREATE) {
                        processEvents(j);
                    } else {
                        pollerCloseable.cancel(true);
                        j.close();
                    }
                });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pollerCloseable.cancel(true);
            axisStream.tryEmitComplete();
            buttonStream.tryEmitComplete();
            j.close();
        }));

        return GamepadStateStream.builder()
                .axisFlux(axisStream.asFlux())
                .buttonFlux(buttonStream.asFlux())
                .build();
    }

    @SneakyThrows
    public void processEvents(LinuxJoystick j) {
        Thread.sleep(100);
        j.open();

        GamepadInputGroupQuery<Boolean> buttonStatusGamepad = gamepadWith(j::getButtonState);
        GamepadInputGroupQuery<Integer> axisStateGamepad = gamepadWith(j::getAxisState);

        pollerCloseable = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (!j.isDeviceOpen()) return;

            j.poll();

            if (!j.isChanged()) return;

            Gamepad gamepadBtn = Arrays.stream(EButtonGamepadEvt.values())
                    .reduce(gamepad, (q, p) -> p.accState(buttonStatusGamepad).apply(q, p), laterMerger);
            buttonStream.tryEmitNext(gamepadBtn);

            Gamepad gamepadAxs = Arrays.stream(EAxisGamepadEvt.values())
                    .reduce(gamepad, (q, p) -> p.accState(axisStateGamepad).apply(q, p), laterMerger);
            axisStream.tryEmitNext(gamepadAxs);

        }, 0, 80, TimeUnit.MILLISECONDS);
    }

    <T> GamepadInputGroupQuery<T> gamepadWith(Function<Integer, T> getter) {
        return idx -> witter -> witter.setTriggered(getter.apply(idx));
    }

    BinaryOperator<Gamepad> laterMerger = (p, q) -> q;
}