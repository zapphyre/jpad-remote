package org.asmus.tool;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.ButtonClick;
import org.asmus.model.Gamepad;
import org.asmus.model.TimedValue;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@UtilityClass
public class GamepadIntrospector {

    Map<String, TimedValue> values = new HashMap<>();

    @Getter
    Set<TimedValue> holding = new LinkedHashSet<>();
    Set<String> modifiers = new HashSet<>();

    static {
        try {
            Arrays.stream(Introspector.getBeanInfo(Gamepad.builder().build().getClass()).getPropertyDescriptors())
                    .map(toTimedValueFor(Gamepad.builder().build()))
                    .forEach(q -> values.put(q.getName(), q));

            log.info("introspector initialized #{} states of Gamepad buttons", values.size());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    Function<TimedValue, ButtonClick> pairWithPreviousValue = current -> {
        TimedValue previous = values.get(current.getName());

        if (!current.equals(previous))
            values.put(current.getName(), current);

        return ButtonClick.builder()
                .push(previous)
                .release(current)
                .build();
    };

    @SneakyThrows
    public ButtonClick releaseEvent(Gamepad gamepad) {
        return Arrays.stream(Introspector.getBeanInfo(gamepad.getClass()).getPropertyDescriptors())
                .map(toTimedValueFor(gamepad))
                .map(pairWithPreviousValue)
                .filter(buttonStateChanged)
                .filter(buttonWasPressedAndReleased)
                .filter(notModifier)
                .reduce(lastElement)
                .map(q -> q.withModifiers(popAllModifiers()))
                .orElse(null);
    }

    Set<TimedValue> popAllModifiers() {
        holding.stream()
                .map(TimedValue::getName)
                .forEach(modifiers::add);

        return new HashSet<>(holding);
    }

    BinaryOperator<ButtonClick> lastElement = (p, q) -> q;

    Predicate<ButtonClick> buttonStateChanged = q -> q.getPush().isValue() != q.getRelease().isValue();
    Predicate<ButtonClick> notModifier = q -> !modifiers.remove(q.getPush().getName());
    Predicate<ButtonClick> buttonWasPressed = q -> holding.add(q.getPush());
    Predicate<ButtonClick> buttonWasReleased = q -> holding.remove(q.getRelease()) && holding.remove(q.getPush());
    Predicate<ButtonClick> buttonWasPressedAndReleased = buttonWasPressed.and(buttonWasReleased);

    Function<PropertyDescriptor, TimedValue> toTimedValueFor(Gamepad gamepad) {
        return descriptor -> TimedValue.builder()
                .name(descriptor.getName())
                .value(invokeRealSafe(gamepad).apply(descriptor))
                .build();
    }

    Function<PropertyDescriptor, Boolean> invokeRealSafe(Gamepad gamepad) {
        return descriptor -> {
            try {
                return Boolean.valueOf(descriptor.getReadMethod().invoke(gamepad).toString());
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        };
    }

}
