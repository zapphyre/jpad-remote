package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
public class AxisDigitizer {

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;


}
