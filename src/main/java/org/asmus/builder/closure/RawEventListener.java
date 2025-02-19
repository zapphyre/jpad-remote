package org.asmus.builder.closure;

import org.asmus.model.TimedValue;
import org.asmus.service.JoyWorker;
import reactor.core.publisher.Flux;

import java.util.List;

@FunctionalInterface
public interface RawEventListener {

    Flux<List<TimedValue>> gamepadChange(JoyWorker joyWorker);
}
