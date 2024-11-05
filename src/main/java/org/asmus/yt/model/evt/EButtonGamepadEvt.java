package org.asmus.yt.model.evt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadButtonSetter;
import org.asmus.yt.model.Gamepad;
import org.asmus.yt.model.Reducable;

@Getter
@RequiredArgsConstructor
public enum EButtonGamepadEvt implements Reducable<Boolean> {

    A(0, g -> g::setA),
    B(1, g -> g::setB),
    X(2, g -> g::setX),
    Y(3, g -> g::setY),
    ;

    final int num;
    final GamepadButtonSetter<Boolean> setter;

    @Override
    public ButtonSetter<Boolean> getSetterFun(Gamepad gamepad) {
        return setter.setOn(gamepad);
    }
}
