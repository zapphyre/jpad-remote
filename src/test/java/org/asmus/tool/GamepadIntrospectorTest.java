package org.asmus.tool;

import org.asmus.model.Gamepad;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GamepadIntrospectorTest {

    @Test
    void reflection() {
        Gamepad gpad = Gamepad.builder()
//                .BACK(true)
                .build();

        GamepadIntrospector.releaseEvent(gpad);

        Assertions.assertFalse(gpad.isBACK());
    }
}
