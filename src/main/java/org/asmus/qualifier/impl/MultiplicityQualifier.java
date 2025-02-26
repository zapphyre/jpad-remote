package org.asmus.qualifier.impl;

import org.asmus.model.*;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MultiplicityQualifier extends BaseQualifier {

    Map<ButtonEvent, TimeFuture> timingFutureMap = new HashMap<>();

    void propagateEvent(ButtonEvent evt) {
        Optional.ofNullable(evt)
                .map(timingFutureMap::remove)
                .map(this::map)
                .ifPresent(qualifiedEventStream::tryEmitNext);
    }

    protected boolean computeIsLongPress(ButtonClick tvPair) {
        return tvPair.getRelease().getTime() - tvPair.getPush().getTime() > longStep;
    }

    @Override
    public void qualify(ButtonClick evt) {
        ButtonEvent event = map(evt);

        // first push
        if (!event.isRelease() && !isActive(event)) {
            timingFutureMap.put(event, new TimeFuture(
                            System.currentTimeMillis(), 1, false, event,
                            null)
            );

            return;
        }

        // next push; event unfired and within multiplicity chaining time
        if (!event.isRelease() && isActive(event) && isChainingPossible(event)) {
            TimeFuture timeFuture = timingFutureMap.remove(event);
            timeFuture.future.cancel(true);

            TimeFuture tf = new TimeFuture(System.currentTimeMillis(), timeFuture.multiplicity + 1, timeFuture.longClick, event, null);
            timingFutureMap.put(event, tf);

            return;
        }

        if (event.isRelease() && isActive(event)) {
            TimeFuture timeFuture = timingFutureMap.remove(event);

            long now = System.currentTimeMillis();
            long delta = now - timeFuture.time;
            boolean longClick = delta > 150;

            ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor()
                    .schedule(() -> propagateEvent(event), longStep, TimeUnit.MILLISECONDS);
            TimeFuture tf = new TimeFuture(now, timeFuture.multiplicity, longClick, event, future);
            timingFutureMap.put(event, tf);
        }
    }

    boolean isChainingPossible(ButtonEvent evt) {
        return System.currentTimeMillis() - timingFutureMap.get(evt).time < 2100;
    }

    boolean isActive(ButtonEvent evt) {
        return timingFutureMap.containsKey(evt);
    }

    GamepadEvent map(TimeFuture tf) {
        return GamepadEvent.builder()
                .multiplicity(EMultiplicity.getByClickCount(tf.multiplicity))
                .eventName(tf.evt.getName())
                .type(EButtonAxisMapping.getByName(tf.evt.getName()))
                .longPress(tf.longClick)
                .qualified(EQualificationType.MULTIPLE)
                .build();
    }

    ButtonEvent map(ButtonClick click) {
        return ButtonEvent.builder()
                .name(click.getPush().getName())
                .release(click.getPush().isValue())
                .build();
    }

    record TimeFuture(long time, int multiplicity, boolean longClick, ButtonEvent evt, ScheduledFuture<?> future) {
    }
}
