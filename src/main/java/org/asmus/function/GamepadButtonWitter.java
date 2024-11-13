package org.asmus.function;

import org.asmus.model.Gamepad;

@FunctionalInterface
public interface GamepadButtonWitter<T> {

    ButtonSetter<T> setOn(Gamepad gamepad);
}
