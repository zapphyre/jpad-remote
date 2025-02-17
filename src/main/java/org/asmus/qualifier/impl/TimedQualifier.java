package org.asmus.qualifier.impl;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.ButtonClick;
import org.asmus.model.EMultiplicity;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class TimedQualifier extends BaseQualifier {

    Map<ButtonClick, PendingClick> scheduledActionsMap = new HashMap<>();

    @Override
    public BiConsumer<ButtonClick, SynchronousSink<GamepadEvent>> qualify() {
        return (evt, sink) -> {
            if (scheduledActionsMap.containsKey(evt))
                propagateEvent(evt, sink, EMultiplicity.DOUBLE);
            else
                scheduledActionsMap.put(evt, new PendingClick(Executors.newSingleThreadScheduledExecutor().schedule(() -> propagateEvent(evt, sink, EMultiplicity.CLICK), 200, TimeUnit.MILLISECONDS), toGamepadEventWith(evt)));
        };
    }

    void propagateEvent(ButtonClick evt, SynchronousSink<GamepadEvent> sink, EMultiplicity multiplicity) {
        Optional.ofNullable(evt)
                .map(scheduledActionsMap::remove)
                .filter(f -> f.future.cancel(true))
                .map(PendingClick::gamepadEvent)
                .map(p -> p.withMultiplicity(multiplicity))
                .ifPresent(sink::next);
    }

    protected boolean computeIsLongPress(ButtonClick tvPair) {
        return tvPair.getRelease().getTime() - tvPair.getPush().getTime() > longStep;
    }

    public record PendingClick(ScheduledFuture<?> future, GamepadEvent gamepadEvent) {
    }
}
