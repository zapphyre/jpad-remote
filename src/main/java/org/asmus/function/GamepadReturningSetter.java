package org.asmus.function;

import org.asmus.model.Gamepad;

@FunctionalInterface
public interface GamepadReturningSetter<T> {

    Gamepad targetReturningSetter(ButtonSetter<T> setter);
}
