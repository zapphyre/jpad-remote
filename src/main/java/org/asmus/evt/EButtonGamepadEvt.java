package org.asmus.evt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadButtonSetter;
import org.asmus.model.Gamepad;
import org.asmus.model.Reducible;

@Getter
@RequiredArgsConstructor
public enum EButtonGamepadEvt implements Reducible<Boolean> {

    A(0, g -> g::withA),
    B(1, g -> g::withB),
    X(2, g -> g::withX),
    Y(3, g -> g::withY),
    LEFT_STICK_CLICK(9, g -> g::withLEFT_STICK_CLICK),
    ;

    final int num;
    final GamepadButtonSetter<Boolean> setter;

    @Override
    public ButtonSetter<Boolean> getSetterFun(Gamepad gamepad) {
        return setter.setOn(gamepad);
    }
}
