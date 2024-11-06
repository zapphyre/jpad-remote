package org.asmus.tool;

import lombok.SneakyThrows;
import lombok.Value;
import org.asmus.model.GEvent;
import org.asmus.model.Gamepad;
import org.asmus.model.TimedValue;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Value
public class GamepadIntrospector {

    Map<String, TimedValue> values = new HashMap<>();

    Function<TimedValue, TVPair> pairWithPreviousValue = q -> new TVPair(q, values.put(q.getName(), q));
    Function<Gamepad, Function<PropertyDescriptor, TimedValue>> toTimedValue = g -> q -> TimedValue.builder()
                    .name(q.getName())
                    .value(invokeRealSafe(g).apply(q))
                    .build();

    @SneakyThrows
    public List<GEvent> introspect(Gamepad gamepad) {

        List<TVPair> list = Arrays.stream(Introspector.getBeanInfo(gamepad.getClass())
                        .getPropertyDescriptors())
                .map(toTimedValueInstance(gamepad).andThen(pairWithPreviousValue))
                .toList();

        return List.of();
    }

    Function<PropertyDescriptor, TimedValue> toTimedValueInstance(Gamepad gamepad) {
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

    record TVPair(TimedValue first, TimedValue second) {
    }
}
