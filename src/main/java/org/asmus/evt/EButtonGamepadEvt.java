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
    X(2, g -> g::withX),
    Y(3, g -> g::withY),
    LEFT_STICK_CLICK(9, g -> g::withLEFT_STICK_CLICK),
    ;

    final int num;
    final GamepadButtonWitter<Boolean> witter;

    @Override
    public ButtonSetter<Boolean> withButtonStateOn(Gamepad gamepad) {
        return witter.setOn(gamepad);
    }
}
