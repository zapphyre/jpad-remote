package org.asmus.tool;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.Gamepad;
import org.asmus.model.TVPair;
import org.asmus.model.TimedValue;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Value
public class GamepadIntrospector {

    static Map<String, TimedValue> values = new HashMap<>();
    static Set<TimedValue> holding = new HashSet<>();

    static Function<TimedValue, TVPair> pairWithPreviousValue = q -> {
        TimedValue previous = values.get(q.getName());

        if (!q.equals(previous))
            values.put(q.getName(), q);

        return TVPair.builder()
                .first(previous)
                .second(q)
                .build();
    };

    static Function<Gamepad, Function<PropertyDescriptor, TimedValue>> toTimedValue = gamepad -> descriptor ->
            TimedValue.builder()
                    .name(descriptor.getName())
                    .value(invokeRealSafe(gamepad).apply(descriptor))
                    .build();


    @SneakyThrows
    static public List<TVPair> introspect(Gamepad gamepad) {
        return Arrays.stream(Introspector.getBeanInfo(gamepad.getClass()).getPropertyDescriptors())
                .map(toTimedValueFor(gamepad).andThen(pairWithPreviousValue))
                .filter(q -> Objects.nonNull(q.getFirst()))
                .filter(q -> holding.add(q.getFirst()))
                .filter(q -> holding.remove(q.getSecond()) && holding.remove(q.getFirst()))
                .toList();
    }

    static Function<PropertyDescriptor, TimedValue> toTimedValueFor(Gamepad gamepad) {
        return toTimedValue.apply(gamepad);
    }

    static Function<PropertyDescriptor, String> invokeRealSafe(Gamepad gamepad) {
        return descriptor -> {
            try {
                return descriptor.getReadMethod().invoke(gamepad).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        };
    }

}
