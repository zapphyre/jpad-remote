package org.asmus.builder.closure.axis;

import org.asmus.model.TriggerPosition;
import org.asmus.service.JoyWorker;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface TriggerPosProducer {

    Flux<TriggerPosition> polarProducer(JoyWorker worker);
}
