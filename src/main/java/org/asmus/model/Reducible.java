package org.asmus.model;

import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadInputGroupQuery;

import java.util.function.BiFunction;

public interface Reducible<T> {

    int getNum();

    ButtonSetter<T> getSetterFun(Gamepad gamepad);

    default BiFunction<Gamepad, Reducible<T>, Gamepad> getReducer(GamepadInputGroupQuery<T> query) {
        return (gamepad, evt) -> query
                .getValueForIndex(evt.getNum())
                .targetReturningSetter(getSetterFun(gamepad));
    }
}
