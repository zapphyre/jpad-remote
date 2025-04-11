package org.asmus.qualifier.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.model.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MultiplicityQualifier extends BaseQualifier {

    Map<ButtonEvent, TimeFuture> timingFutureMap = new HashMap<>();
    Set<EButtonAxisMapping> modifiers = new HashSet<>();

    void propagateEvent(ButtonEvent evt) {
        Optional.ofNullable(evt)
                .map(timingFutureMap::remove)
                .map(this::map)
                .filter(q -> modifiers.isEmpty() || !modifiers.removeAll(Set.of(q.getType())))
                .filter(q -> q.getModifiers().isEmpty() || modifiers.addAll(q.getModifiers()))
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

        // last release of previously recorded push
        if (event.isRelease() && isActive(event)) {
            TimeFuture timeFuture = timingFutureMap.remove(event);

            long now = System.currentTimeMillis();
            long delta = now - timeFuture.time;
            boolean longClick = delta > 410;
//            System.out.println("delta: " + delta);

            ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor()
                    .schedule(() -> propagateEvent(event), longStep, TimeUnit.MILLISECONDS);
            TimeFuture tf = new TimeFuture(now, timeFuture.multiplicity, longClick, event, future);
            timingFutureMap.put(event, tf);
        }
    }

    boolean isChainingPossible(ButtonEvent evt) {
        return System.currentTimeMillis() - timingFutureMap.get(evt).time < 210;
    }

    boolean isActive(ButtonEvent evt) {
        return timingFutureMap.containsKey(evt);
    }

    GamepadEvent map(TimeFuture tf) {
        return GamepadEvent.builder()
                .multiplicity(EMultiplicity.getByClickCount(tf.multiplicity))
                .modifiers(tf.evt.getModifiers().stream().map(EButtonAxisMapping::getByMappingName).collect(Collectors.toSet()))
                .eventName(tf.evt.getName())
                .type(EButtonAxisMapping.getByMappingName(tf.evt.getName()))
                .longPress(tf.longClick)
                .qualified(EQualificationType.MULTIPLE)
                .build();
    }

    ButtonEvent map(ButtonClick click) {
        return ButtonEvent.builder()
                .name(click.getPush().getName())
                .release(click.getPush().isValue())
                .modifiers(click.getModifiers())
                .build();
    }

    record TimeFuture(long time, int multiplicity, boolean longClick, ButtonEvent evt, ScheduledFuture<?> future) {
    }
}
