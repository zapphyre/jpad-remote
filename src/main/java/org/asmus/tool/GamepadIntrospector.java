package org.asmus.tool;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.Value;
import org.asmus.model.GEvent;
import org.asmus.model.Gamepad;
import org.asmus.model.TimedValue;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

@Value
public class GamepadIntrospector {

    Map<String, TimedValue> values = new HashMap<>();

    Function<TimedValue, TVPair> pairWithPreviousValue = q -> {
        TimedValue previous = values.get(q.getName());
        if (!q.equals(previous))
            values.put(q.getName(), q);

        return new TVPair(q, previous);
    };

    Function<Gamepad, Function<PropertyDescriptor, TimedValue>> toTimedValue = gamepad -> descriptor ->
            TimedValue.builder()
                    .name(descriptor.getName())
                    .value(invokeRealSafe(gamepad).apply(descriptor))
                    .build();

    Set<TimedValue> holding = new HashSet();

    @SneakyThrows
    public List<GEvent> introspect(Gamepad gamepad) {

        List<TVPair> list = Arrays.stream(Introspector.getBeanInfo(gamepad.getClass()).getPropertyDescriptors())
                .map(toTimedValueFor(gamepad).andThen(pairWithPreviousValue))
                .filter(q -> !q.getFirst().equals(q.second))
                .filter(q -> Objects.nonNull(q.getSecond()))
                .filter(q -> holding.add(q.getFirst()))
                .filter(q -> holding.remove(q.getSecond()))
                .toList();

        return List.of();
    }

    Function<PropertyDescriptor, TimedValue> toTimedValueFor(Gamepad gamepad) {
        return toTimedValue.apply(gamepad);
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

    @Value
    @Builder
    @EqualsAndHashCode
    static class TVPair {
        TimedValue first;
        TimedValue second;
    }
}
