package org.asmus.function;

import org.asmus.yt.model.Gamepad;

@FunctionalInterface
public interface GamepadButtonSetter<T> {

    ButtonSetter<T> setOn(Gamepad gamepad);
}
