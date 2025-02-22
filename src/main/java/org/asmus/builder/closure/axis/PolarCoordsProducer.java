package org.asmus.builder.closure.axis;

import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface PolarCoordsProducer {

    Flux<PolarCoords> polarProducer(JoyWorker worker);
}
