package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;
import org.asmus.model.EMultiplicity;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class AutoLongClickQualifier extends TimedQualifier {

    Map<ButtonClick, PendingClick> scheduledActionsMap = new HashMap<>();

    @Override
    public BiConsumer<ButtonClick, SynchronousSink<GamepadEvent>> qualify() {
        return (evt, sink) -> {
//            Optional.ofNullable(evt.getPush())
//                    .orElse(evt.getRelease())
//                    .getName();

            if (evt.getPush().isValue() && !scheduledActionsMap.containsKey(evt))
                return;

            if (scheduledActionsMap.containsKey(evt))
                propagateEvent(evt, sink, EMultiplicity.DOUBLE);
            else
                scheduledActionsMap.put(evt, new PendingClick(Executors.newSingleThreadScheduledExecutor().schedule(() -> propagateEvent(evt, sink, EMultiplicity.CLICK), 200, TimeUnit.MILLISECONDS), toGamepadEventWith(evt)));
        };
    }

    void propagateEvent(ButtonClick evt, SynchronousSink<GamepadEvent> sink, EMultiplicity multiplicity) {
        Optional.ofNullable(evt)
                .map(scheduledActionsMap::remove)
                .filter(f -> f.future().cancel(true))
                .map(PendingClick::gamepadEvent)
                .map(p -> p.withMultiplicity(multiplicity))
                .ifPresent(sink::next);
    }

}
