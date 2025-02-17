package org.asmus.qualifier.impl;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.ButtonClick;
import org.asmus.model.EMultiplicity;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class TimedQualifier extends BaseQualifier {

    Map<ButtonClick, PendingClick> scheduledActionsMap = new HashMap<>();

    public TimedQualifier(Sinks.Many<GamepadEvent> output) {
        super(output);
    }

    public BiConsumer<ButtonClick, SynchronousSink<GamepadEvent>> qualify() {
        return (evt, sink) -> {
            if (scheduledActionsMap.containsKey(evt))
                propagateEvent(evt, EMultiplicity.DOUBLE);
            else
                scheduledActionsMap.put(evt, new PendingClick(Executors.newSingleThreadScheduledExecutor().schedule(() -> propagateEvent(evt, EMultiplicity.CLICK), 200, TimeUnit.MILLISECONDS), toGamepadEventWith(evt)));
        };
    }

    void propagateEvent(ButtonClick evt, EMultiplicity multiplicity) {
        Optional.ofNullable(evt)
                .map(scheduledActionsMap::remove)
                .filter(f -> f.future.cancel(true))
                .map(PendingClick::gamepadEvent)
                .map(p -> p.withMultiplicity(multiplicity))
                .ifPresent(output::tryEmitNext);
    }

    protected boolean computeIsLongPress(ButtonClick tvPair) {
        return tvPair.getRelease().getTime() - tvPair.getPush().getTime() > longStep;
    }

    @Override
    public void qualify(ButtonClick evt) {
        if (scheduledActionsMap.containsKey(evt))
            propagateEvent(evt, EMultiplicity.DOUBLE);
        else
            scheduledActionsMap.put(evt, new PendingClick(Executors.newSingleThreadScheduledExecutor().schedule(() -> propagateEvent(evt, EMultiplicity.CLICK), longStep, TimeUnit.MILLISECONDS), toGamepadEventWith(evt)));
    };


    public record PendingClick(ScheduledFuture<?> future, GamepadEvent gamepadEvent) {
    }
}
