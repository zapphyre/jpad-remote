package org.asmus.yt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum EButtonGamepadEvt implements Reducable<Boolean> {
    A(0, g -> g::setA),
    B(1, g -> g::setB),
    X(2, g -> g::setX),
    Y(3, g -> g::setY),
    ;

    final int num;
    final Function<Gamepad, Consumer<Boolean>> setter;

    @Override
    public Function<Gamepad, Consumer<Boolean>> getSetterFun() {
        return setter;
    }
}
