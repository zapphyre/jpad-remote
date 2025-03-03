package org.asmus.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.*;
import org.asmus.service.JoyWorker;
import org.asmus.tool.AxisMapper;
import org.asmus.tool.EventMapper;
import org.asmus.introspect.impl.ReleaseIntrospector;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fs.watcher.FsWatcher.watch;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

@Slf4j
public class EventProducer {
    static ObjectMapper mapper = new ObjectMapper();

    ReleaseIntrospector introspector = new ReleaseIntrospector();

    @Getter
    JoyWorker worker = new JoyWorker();

    public List<Runnable> watchForDevices(Integer ...ids) {
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
            AtomicReference<Runnable> teardown = new AtomicReference<>(() -> {});

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

    public static Controller getControllerMappings(String path) {
        try {

            InputStream in;
            in = EventProducer.class.getResourceAsStream("/lib/gamepadPropsParametric");
            if (in == null) {
                in = Files.newInputStream(Path.of("lib/gamepadPropsParametric"));
            }

            Path tempFile = Files.createTempFile("gamepadPropsParametric", null);
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().setExecutable(true);

            ProcessBuilder processBuilder = new ProcessBuilder(tempFile.toString(), path);

            Thread.sleep(200);
            Process process = processBuilder.start();

            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String stdOutStr = stdOut.lines()
                    .collect(Collectors.joining(System.lineSeparator()));

            log.info(stdOutStr);
            return mapper.readValue(stdOutStr, Controller.class);
        } catch (IOException | InterruptedException e) {
        }

        return null;
    }
}
