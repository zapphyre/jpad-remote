package org.asmus.yt.model;

import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadInputGroupQuery;

import java.util.function.BiFunction;

public interface Reducable<T> {

    int getNum();

    ButtonSetter<T> getSetterFun(Gamepad gamepad);

    default BiFunction<Gamepad, Reducable<T>, Gamepad> getReducer(GamepadInputGroupQuery<T> query) {
        return (gamepad, evt) -> query.getValueForComponent(evt.getNum())
                .targetReturningSetter(getSetterFun(gamepad));
    }
}
