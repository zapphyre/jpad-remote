package org.asmus.function;

@FunctionalInterface
public interface ButtonSetter<T> {

    void setTriggered(T triggered);
}
