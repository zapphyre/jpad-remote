package org.asmus.tool;

import org.asmus.model.ButtonClick;
import org.asmus.model.InputValue;
import org.asmus.model.TimedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GamepadIntrospectorTest {

    private GamepadIntrospector introspector;

    @BeforeEach
    void setUp() {
        introspector = new GamepadIntrospector();
    }

    @Test
    void testButtonStateChanged() {
        TimedValue press = new TimedValue(new InputValue<Boolean>(true, "A"));
        TimedValue release = new TimedValue(new InputValue<Boolean>(false, "A"));

        ButtonClick click = ButtonClick.builder().push(press).release(release).build();

        assertTrue(introspector.buttonStateChanged.test(click), "Button state should be considered changed.");
    }

    @Test
    void testNotModifier() {
        TimedValue press = new TimedValue(new InputValue<Boolean>(true, "A"));
        introspector.modifiers.add("A");

        ButtonClick click = ButtonClick.builder().push(press).build();

        assertFalse(introspector.notModifier.test(click), "Button should be recognized as a modifier.");
    }

    @Test
    void testButtonWasPressedAndReleased() {
        TimedValue press = new TimedValue(new InputValue<>(true, "A"));
        TimedValue release = new TimedValue(new InputValue<>(false, "A"));

        TimedValue press2 = new TimedValue(new InputValue<>(false, "A"));
        TimedValue release2 = new TimedValue(new InputValue<>(true, "A"));

        ButtonClick click = ButtonClick.builder().push(press).release(release).build();
        ButtonClick click2 = ButtonClick.builder().push(press2).release(release2).build();

        // Simulate pressing
        introspector.buttonWasPressedAndReleased.test(click);
        // simulate and releasing
        boolean released = introspector.buttonWasPressedAndReleased.test(click2);

        assertTrue(released, "Button should be recognized as pressed and then released.");
    }

    @Test
    void testPairWithPreviousValue() {
        TimedValue firstValue = new TimedValue(new InputValue<Boolean>(true, "A"));
        TimedValue secondValue = new TimedValue(new InputValue<Boolean>(false, "A"));

        // First call, previous should be default (false)
        ButtonClick click1 = introspector.pairWithPreviousValue.apply(firstValue);
        assertFalse(click1.getPush().isValue());
        assertTrue(click1.getRelease().isValue());

        // Second call, now the firstValue is stored as previous
        ButtonClick click2 = introspector.pairWithPreviousValue.apply(secondValue);
        assertTrue(click2.getPush().isValue());
        assertFalse(click2.getRelease().isValue());
    }

    @Test
    void testReleaseEvent() {
        TimedValue press = new TimedValue(new InputValue<>(true, "a"));
        TimedValue mod = new TimedValue(new InputValue<>(true, "b"));
        TimedValue release = new TimedValue(new InputValue<>(false, "a"));

        TimedValue modRelease = new TimedValue(new InputValue<>(false, "b"));

        ButtonClick result = introspector.releaseEvent(List.of(press, mod, release));
        introspector.releaseEvent(List.of(modRelease));

        assertNotNull(result, "ButtonClick event should not be null.");
        assertEquals(press.getName(), result.getPush().getName());
        assertTrue(result.getModifiers().contains("b"), "First press should contain modifier");
        assertFalse(introspector.modifiers.contains("b"), "Modifier should be reset after release.");
    }
}
