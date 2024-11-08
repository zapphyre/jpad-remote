package org.asmus.evt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadButtonSetter;
import org.asmus.model.Reducible;
import org.asmus.model.Gamepad;

@Getter
@RequiredArgsConstructor
public enum EAxisGamepadEvt implements Reducible<Integer> {

    LEFT_STICK_X(0, g -> g::withLEFT_STICK_X),
    LEFT_STICK_Y(1, g -> g::withLEFT_STICK_Y),
//    LEFT_STICK_LEFT(0, g -> g::withLEFT_STICK_LEFT),
//    LEFT_STICK_RIGHT(0, g -> g::withLEFT_STICK_RIGHT),

//    RIGHT_STICK_UP(0, g -> g::withDPAD_UP),
//    RIGHT_STICK_DOWN(1, g -> g::withDPAD_DOWN),
//    RIGHT_STICK_LEFT(2, g -> g::withDPAD_LEFT),
//    RIGHT_STICK_RIGHT(3, g -> g::withDPAD_RIGHT),
    ;

    final int num;
    final GamepadButtonSetter<Integer> setter;

    @Override
    public ButtonSetter<Integer> getSetterFun(Gamepad gamepad) {
        return setter.setOn(gamepad);
    }
}
