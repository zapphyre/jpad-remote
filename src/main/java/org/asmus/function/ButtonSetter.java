package org.asmus.function;

import org.asmus.model.Gamepad;

@FunctionalInterface
public interface ButtonSetter<T> {

    Gamepad setTriggered(T triggered);
}
