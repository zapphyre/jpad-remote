package org.asmus.model;

import org.asmus.function.ButtonSetter;
import org.asmus.function.GamepadInputGroupQuery;

import java.util.function.BiFunction;

public interface EPadEventReducible<T> {

    int getNum();

    ButtonSetter<T> getWitterFun(Gamepad gamepad);

    default BiFunction<Gamepad, EPadEventReducible<T>, Gamepad> getReducer(GamepadInputGroupQuery<T> query) {
        return (gamepad, evt) -> query
                .getValueForIndex(evt.getNum())
                .targetReturningSetter(getWitterFun(gamepad));
    }
}
