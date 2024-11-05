package org.asmus.yt.model;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Reducable<T> {

    int getNum();

    Function<Gamepad, Consumer<T>> getSetterFun();

    default BiFunction<Gamepad, Reducable<T>, Gamepad> getReducer(Function<Integer, Function<Consumer<T>, Gamepad>> buttonQuery) {
        return (gamepad, evt) -> buttonQuery.apply(evt.getNum()).apply(evt.getSetterFun().apply(gamepad));
    }
//
//    static <T> BiFunction<Gamepad, Reducable<T>, Gamepad> reducer(Function<Integer, Function<Consumer<T>, Gamepad>> buttonQuery) {
//        return (gamepad, evt) -> buttonQuery.apply(evt.getNum()).apply(evt.getSetterFun().apply(gamepad));
//    }
}
