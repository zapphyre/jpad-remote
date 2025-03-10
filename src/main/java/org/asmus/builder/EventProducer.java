package org.asmus.builder;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.SDLJoystick;
import org.asmus.model.Controller;
import org.asmus.model.GamepadDbFileRow;
import org.asmus.service.JoyWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static fs.watcher.FsWatcher.watch;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

@Slf4j
public class EventProducer {

    @Getter
    JoyWorker worker = new JoyWorker();

    public List<Runnable> watchForDevices(Integer... ids) {
        return Arrays.stream(ids)
                .map("/dev/input/js%01d"::formatted)
                .peek(watchFsEvents(ENTRY_CREATE, ENTRY_DELETE))
                .map(EventProducer::getControllerMappings)
                .filter(Objects::nonNull)
                .filter(pathExists)
                .map(worker::watchingDevice)
                .toList();
    }

    Consumer<String> watchFsEvents(WatchEvent.Kind<?>... events) {
        return q -> {
            AtomicReference<Runnable> teardown = new AtomicReference<>(() -> {
            });

            try {

                watch(Path.of(q))
                        .forEvents(events)
                        .onChange(c -> {

                            if (c.kind() == ENTRY_CREATE)
                                Optional.of(c.path())
                                        .map(Path::toString)
                                        .map(EventProducer::getControllerMappings)
                                        .map(worker::watchingDevice)
                                        .ifPresent(teardown::set);
                            else
                                teardown.get().run();

                        });

            } catch (IOException e) {
                Optional.ofNullable(teardown.get())
                        .ifPresent(Runnable::run);
            }
        };
    }

    static Predicate<Controller> pathExists = q -> Files.exists(Path.of(q.device()));

    @SneakyThrows
    public static Controller getControllerMappings(String path) {
        System.out.println("trying to get mapping for " + path);
        int index = Integer.parseInt(path.substring(path.length() - 1));
        SDLJoystick sdl = null;

        String controllerMapping;
        String joystickName;
        int axis;
        int joystickNumButtons;

        try {
            sdl = new SDLJoystick(index);
            boolean open = sdl.open();

            if (!open)
                return null;

            axis = sdl.getJoystickNumAxes() + sdl.getJoystickNumHats() * 2;
            controllerMapping = sdl.getControllerMapping();
            joystickName = sdl.getJoystickName();
            joystickNumButtons = sdl.getJoystickNumButtons();
        } catch (Exception e) {
            System.err.println("error while initializing SDL joystick: " + e.getMessage());
            return null;
        } finally {
            Optional.ofNullable(sdl).ifPresent(SDLJoystick::close);
        }

        System.out.println("recognized controller name: " + joystickName);
        System.out.println("controller mapping: " + controllerMapping);

        return new Controller(axis, joystickNumButtons, path, controllerMapping, joystickName);
    }
}
