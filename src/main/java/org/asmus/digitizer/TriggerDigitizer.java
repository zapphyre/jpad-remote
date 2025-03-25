package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class TriggerDigitizer {

    private final long QUICK_MS = 210;
    public static final int MAX = 32767;
    private final int MIN = -32767;
    private EButtonAxisMapping last;

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;

    private TriggerState current = new Released();

    public Consumer<TriggerPosition> digitize() {
        return q -> {
            current = current.getNext(q.getPosition(), current);

            qualifiedEventStream.tryEmitNext(GamepadEvent.builder()
                    .qualified(EQualificationType.PUSH)
                    .type(last = current.getEmittent())
                    .modifiers(q.getModifiers())
                    .build());
        };
    }

    boolean isQuickClick(TriggerState state) {
        return System.currentTimeMillis() - state.getTriggerTime() < QUICK_MS;
    }

    abstract class TriggerState {
        long triggerTime = System.currentTimeMillis();

        abstract TriggerState getNext(int pos, TriggerState prev);

        abstract EButtonAxisMapping getEmittent();

        long getTriggerTime() {
            return triggerTime;
        }
    }

    class Released extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX && prev instanceof Released) return new Engaged();
            if (pos == MIN) return new StepDown();

            return this;
        }

        @Override
        EButtonAxisMapping getEmittent() {
            return EButtonAxisMapping.RELEASE;
        }
    }

    class Engaged extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new StepUp();
            if (pos == MIN) return new Released();

            return this;
        }

        @Override
        EButtonAxisMapping getEmittent() {
            return EButtonAxisMapping.ENGAGE;
        }
    }

    class StepUp extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new StepUp();
            if (pos == MIN) return new Released();

            return this;
        }

        @Override
        EButtonAxisMapping getEmittent() {
            return EButtonAxisMapping.STEP_POSITIVE;
        }
    }

    class StepDown extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new Engaged();
            if (pos == MIN) return new StepDown();

            return this;
        }

        @Override
        EButtonAxisMapping getEmittent() {
            return EButtonAxisMapping.STEP_NEGATIVE;
        }
    }
}
