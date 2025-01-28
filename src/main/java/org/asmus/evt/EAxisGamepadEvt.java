package org.asmus.evt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadButtonWitter;
import org.asmus.model.EPadEventReducible;
import org.asmus.model.Gamepad;

@Getter
@RequiredArgsConstructor
public enum EAxisGamepadEvt implements EPadEventReducible<Integer> {

    LEFT_STICK_X(0, g -> g::withLEFT_STICK_X),
    LEFT_STICK_Y(1, g -> g::withLEFT_STICK_Y),

    RIGHT_STICK_X(2, g -> g::withRIGHT_STICK_X),
    RIGHT_STICK_Y(3, g -> g::withRIGHT_STICK_Y),

    TRIGGER_RIGHT(4, g -> g::withTRIGGER_RIGHT),
    TRIGGER_LEFT(5, g -> g::withTRIGGER_LEFT),

    HORIZ(6, g -> g::withHORIZONTAL_BTN),
    VERT(7, g -> g::withVERTICAL_BTN),
    ;

    final int num;
    final GamepadButtonWitter<Integer> setter;

    @Override
    public ButtonSetter<Integer> withButtonStateOn(Gamepad gamepad) {
        return setter.setOn(gamepad);
    }
}
