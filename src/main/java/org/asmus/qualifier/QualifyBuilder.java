package org.asmus.qualifier;

import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;

public interface QualifyBuilder {


    Qualifier useStream(Sinks.Many<GamepadEvent> stream);
}
