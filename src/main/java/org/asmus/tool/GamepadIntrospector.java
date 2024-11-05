package org.asmus.tool;

import lombok.SneakyThrows;
import lombok.Value;
import org.asmus.model.GEvent;
import org.asmus.model.TimedValue;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Value
public class GamepadIntrospector<T> {

    Map<String, TimedValue> values = new HashMap<>();

    Function<TimedValue, TVPair> pairWithPreviousValue = q -> new TVPair(q, values.put(q.getName(), q));
    Function<PropertyDescriptor, TimedValue> toTimedValue = q -> TimedValue.builder()
                    .name(q.getName())
                    .value(q.getValue(q.getName()).toString())
                    .build();

    @SneakyThrows
    public List<GEvent> introspect(T gamepad) {

        Arrays.stream(Introspector.getBeanInfo(gamepad.getClass())
                        .getPropertyDescriptors())
                .map(toTimedValue.andThen(pairWithPreviousValue));
//                .map(q -> )

        return List.of();
    }

    record TVPair(TimedValue first, TimedValue second) {
    }
}
