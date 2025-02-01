package org.asmus.evt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadButtonWitter;
import org.asmus.model.Gamepad;
import org.asmus.model.EPadEventReducible;

@Getter
@RequiredArgsConstructor
public enum EButtonGamepadEvt implements EPadEventReducible<Boolean> {

    A(0, g -> g::withA),
    B(1, g -> g::withB),
    X(3, g -> g::withX),
    Y(4, g -> g::withY),

    BUMPER_LEFT(6, g -> g::withBUMPER_LEFT),
    BUMPER_RIGHT(7, g -> g::withBUMPER_RIGHT),

    START(11, g -> g::withSTART),
    SELECT(10, g -> g::withSELECT),
    HOME(12, g -> g::withHOME),

    LEFT_STICK_CLICK(13, g -> g::withLEFT_STICK_CLICK),
    ;

    final int num;
    final GamepadButtonWitter<Boolean> witter;

    @Override
    public ButtonSetter<Boolean> withButtonStateOn(Gamepad gamepad) {
        return witter.setOn(gamepad);
    }
}
