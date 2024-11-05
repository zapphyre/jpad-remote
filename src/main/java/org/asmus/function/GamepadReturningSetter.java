package org.asmus.function;

import org.asmus.yt.model.Gamepad;

@FunctionalInterface
public interface GamepadReturningSetter<T> {

    Gamepad targetReturningSetter(ButtonSetter<T> setter);
}
