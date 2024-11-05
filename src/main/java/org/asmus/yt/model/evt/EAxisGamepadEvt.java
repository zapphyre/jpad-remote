package org.asmus.yt.model.evt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadButtonSetter;
import org.asmus.yt.model.Gamepad;
import org.asmus.yt.model.Reducable;

@Getter
@RequiredArgsConstructor
public enum EAxisGamepadEvt implements Reducable<Integer> {

    LEFT_STICK_UP(0, g -> g::withDPAD_UP),
    LEFT_STICK_DOWN(1, g -> g::withDPAD_DOWN),
    RIGHT_STICK_LEFT(2, g -> g::withDPAD_LEFT),
    RIGHT_STICK_RIGHT(3, g -> g::withDPAD_RIGHT),
    ;

    final int num;
    final GamepadButtonSetter<Integer> setter;

    @Override
    public ButtonSetter<Integer> getSetterFun(Gamepad gamepad) {
        return setter.setOn(gamepad);
    }
}
