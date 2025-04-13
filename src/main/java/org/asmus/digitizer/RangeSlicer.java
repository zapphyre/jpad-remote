package org.asmus.digitizer;

import static org.asmus.model.NamingConstants.MAX;
import static org.asmus.model.NamingConstants.MIN;

public class RangeSlicer {
    private final int numSegments;
    private int lastSegmentIndex = -1; // Track previous segment

    public RangeSlicer(int numSegments) {
        if (numSegments <= 0) {
            throw new IllegalArgumentException("Number of segments must be positive");
        }
        this.numSegments = numSegments;
    }

    public void processInput(int value) {
        // Validate input

        System.out.println("RangeSlicer.processInput(" + value + ")");
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException("Input must be in range [" + MIN + ", " + MAX + "]");
        }

        // Calculate segment index
        long range = (long) MAX - MIN + 1; // Use long to avoid overflow
        int segmentIndex;
        if (value == MAX) {
            segmentIndex = numSegments - 1; // Ensure MAX falls in last segment
        } else {
            // Map value to segment: [MIN, MAX] -> [0, numSegments-1]
            segmentIndex = (int) (((long) value - MIN) * numSegments / range);
        }

        // Check for boundary crossing
        if (lastSegmentIndex != -1 && lastSegmentIndex != segmentIndex) {
            System.out.println("Boundary crossed: from segment " + lastSegmentIndex + " to " + segmentIndex);
        }

        // Update last segment index
        lastSegmentIndex = segmentIndex;
    }
}
