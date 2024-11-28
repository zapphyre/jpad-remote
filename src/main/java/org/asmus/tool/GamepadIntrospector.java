package org.asmus.tool;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.Gamepad;
import org.asmus.model.TVPair;
import org.asmus.model.TimedValue;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@UtilityClass
public class GamepadIntrospector {

    Map<String, TimedValue> values = new HashMap<>();
    Set<TimedValue> holding = new HashSet<>();

    Function<TimedValue, TVPair> pairWithPreviousValue = current -> {
        TimedValue previous = values.get(current.getName());

        if (!current.equals(previous))
            values.put(current.getName(), current);

        return TVPair.builder()
                .first(previous)
                .second(current)
                .build();
    };

    @SneakyThrows // produces empty list on press event
    static public List<TVPair> introspect(Gamepad gamepad) {
        return Arrays.stream(Introspector.getBeanInfo(gamepad.getClass()).getPropertyDescriptors())
                .map(toTimedValueFor(gamepad))
                .map(pairWithPreviousValue)
                .filter(buttonWasPressedAndReleased)
                .toList();
    }

    //first is always previous value of the button pressed
    Predicate<TVPair> buttonStateHasBeenRecordedOnce = q -> Objects.nonNull(q.getFirst());
    Predicate<TVPair> buttonWasActivated = q -> holding.add(q.getFirst());
    Predicate<TVPair> buttonWasReleased = q -> holding.remove(q.getSecond()) && holding.remove(q.getFirst());

    Predicate<TVPair> buttonWasPressedAndReleased =
            buttonStateHasBeenRecordedOnce.and(buttonWasActivated).and(buttonWasReleased);

    Function<PropertyDescriptor, TimedValue> toTimedValueFor(Gamepad gamepad) {
        return descriptor -> TimedValue.builder()
                .name(descriptor.getName())
                .value(invokeRealSafe(gamepad).apply(descriptor))
                .build();
    }

    Function<PropertyDescriptor, String> invokeRealSafe(Gamepad gamepad) {
        return descriptor -> {
            try {
                return descriptor.getReadMethod().invoke(gamepad).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        };
    }

}
