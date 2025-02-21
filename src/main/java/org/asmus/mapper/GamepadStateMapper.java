package org.asmus.mapper;

import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class GamepadStateMapper {

    private final Map<String, TimedValue> values = new HashMap<>();
    private final Predicate<ButtonClick> buttonStateChanged = q -> q.getPush().isValue() != q.getRelease().isValue();

    ButtonClick translate(TimedValue current) {
        TimedValue previous = values.computeIfAbsent(current.getName(), TimedValue::new);

        if (!current.equals(previous))
            values.put(current.getName(), current);

        return ButtonClick.builder()
                .push(previous)
                .release(current)
                .build();
    }

    public ButtonClick map(TimedValue current) {
        return Optional.of(current)
                .map(this::translate)
                .filter(buttonStateChanged)
                .orElse(null);
    }
}
