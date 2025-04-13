package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

import static org.asmus.model.NamingConstants.MAX;
import static org.asmus.model.NamingConstants.MIN;

@RequiredArgsConstructor
public class RangeDigitizer {

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;
    private final int numSegments = 4;
    private int lastSegmentIndex = -1; // Track previous segment
    long range = (long) MAX - MIN + 1; // Total values: 65535

    public Consumer<TriggerPosition> digitize() {
        return q -> {

            int currentSegment = processInput(q.getPosition());

            if (lastSegmentIndex != -1 && lastSegmentIndex != currentSegment) {
                System.out.println("Boundary crossed: from segment " + lastSegmentIndex + " to " + currentSegment);
            }

            lastSegmentIndex = currentSegment;
        };
    }

    private final long segmentSize = range / numSegments;
    int processInput(int value) {
        int segmentIndex;
        if (value == MAX) {
            segmentIndex = numSegments - 1; // Place MAX in last segment
        } else {
            // Shift value to [0, range-1] and divide by segment size
            long shiftedValue = (long) value - MIN; // [0, 65534]
            segmentIndex = (int) (shiftedValue / segmentSize);
            // Ensure boundary edge cases don't overshoot
            if (segmentIndex >= numSegments) {
                segmentIndex = numSegments - 1;
            }
        }

        return segmentIndex;
    }
}
