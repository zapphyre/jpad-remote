package org.asmus.function;

import org.asmus.yt.model.Gamepad;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface GamepadInputGroupQuery<T> {

    GamepadReturningSetter<T> getValueForComponent(int index);
}
