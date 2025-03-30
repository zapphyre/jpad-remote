package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class AxisDigitizer {

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;

    public Consumer<ELogicalEventType> digitize(EButtonAxisMapping x) {
        return q -> {
            qualifiedEventStream.tryEmitNext(GamepadEvent.builder()
                    .type(x)
                    .logicalEventType(q)
                    .build());
        };
    }
}
