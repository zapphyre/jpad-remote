package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;
import org.asmus.model.EQualificationType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoLongClickQualifier extends BaseQualifier {

    Map<String, Future> scheduledActionsMap = new HashMap<>();

    @Override
    public void qualify(ButtonClick evt) {
        String name = evt.getPush().getName();

        // release event, but was emitted automatically before
        if (evt.getPush().isValue() && !scheduledActionsMap.containsKey(name))
            return;

        if (scheduledActionsMap.containsKey(name)) {
            scheduledActionsMap.get(name).cancel(true);

            qualifiedEventStream.tryEmitNext(toGamepadEventWith(evt).withQualified(EQualificationType.LONG));

            scheduledActionsMap.remove(name);
            return;
        }

        ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            qualifiedEventStream.tryEmitNext(toGamepadEventWith(evt).withQualified(EQualificationType.LONG).withLongPress(true));

            scheduledActionsMap.remove(name);
        }, longStep, TimeUnit.MILLISECONDS);

        scheduledActionsMap.put(name, future);
    }
}
