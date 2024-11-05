package org.asmus.function;

@FunctionalInterface
public interface GamepadInputGroupQuery<T> {

    GamepadReturningSetter<T> getValueForIndex(int index);
}
