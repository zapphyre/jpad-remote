package org.asmus.yt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum EAxisGamepadEvt implements Reducable<Integer> {
    LEFT_STICK_UP(0, g -> g::setDPAD_UP),
    LEFT_STICK_DOWN(1, g -> g::setDPAD_DOWN),
    RIGHT_STICK_LEFT(2, g -> g::setDPAD_LEFT),
    RIGHT_STICK_RIGHT(3, g -> g::setDPAD_RIGHT),
    ;

    final int num;
    final Function<Gamepad, Consumer<Integer>> setter;

    @Override
    public Function<Gamepad, Consumer<Integer>> getSetterFun() {
        return setter;
    }
}
