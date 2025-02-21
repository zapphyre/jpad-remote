package org.asmus.mapper;

import lombok.experimental.UtilityClass;
import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class GamepadStateMapper {

    Map<String, TimedValue> values = new HashMap<>();
    Predicate<ButtonClick> buttonStateChanged = q -> q.getPush().isValue() != q.getRelease().isValue();

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
        ButtonClick buttonClick = Optional.of(current)
                .map(GamepadStateMapper::translate)
                .filter(buttonStateChanged)
                .orElse(null);
        return buttonClick;
    }
}
