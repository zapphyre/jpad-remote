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
import java.util.stream.Stream;

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

    private static final List<GamepadDbFileRow> mappings;

    static {
        mappings = readGamepadFile("gamecontrollerdb.txt");
    }

    public static List<GamepadDbFileRow> readGamepadFile(String resourcePath) {
        ClassLoader classLoader = EventProducer.class.getClassLoader();
        List<GamepadDbFileRow> mappings = new LinkedList<>();

        try (InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Resource not found: " + resourcePath);
                return mappings;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    GamepadDbFileRow row = GamepadDbFileRow.parse(line);
                    if (row != null)
                        mappings.add(row);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading resource file: " + e.getMessage());
        }

        return mappings;
    }

    @SneakyThrows
    public static Controller getControllerMappings(String path) {
        int index = Integer.parseInt(path.substring(path.length() - 1));
        SDLJoystick sdl;

        try {
            sdl = new SDLJoystick(index);
        } catch (Exception e) {
            return null;
        }

        String platform = System.getProperty("os.name");
        int axis = sdl.getJoystickNumAxes() + sdl.getJoystickNumHats() * 2;

        List<GamepadDbFileRow> byGuid = mappings.stream()
                .filter(q -> q.getGuid().equals(sdl.getJoystickGUID()))
                .toList();

        List<GamepadDbFileRow> byName = mappings.stream()
                .filter(q -> q.getName().equalsIgnoreCase(sdl.getJoystickName()))
                .toList();

        GamepadDbFileRow gamepadDef = byGuid.isEmpty() ? byName.getFirst() : byGuid.getFirst();

        if (gamepadDef == null)
            return null;

        return new Controller(axis, sdl.getJoystickNumButtons(), path, gamepadDef.getMapping(), sdl.getJoystickName());
    }
}
