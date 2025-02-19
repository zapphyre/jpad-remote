package org.asmus.qualifier;

import org.asmus.model.ButtonClick;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;

public interface Qualifier {

    void qualify(ButtonClick click);

    Sinks.Many<GamepadEvent> getQualifiedEventStream();
}
