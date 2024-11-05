package org.asmus.function;

import org.asmus.yt.model.Gamepad;

@FunctionalInterface
public interface ButtonSetter<T> {

    Gamepad setTriggered(T triggered);
}
