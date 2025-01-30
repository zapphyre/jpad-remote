package org.asmus.component;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.ButtonClick;
import org.asmus.model.EMultiplicity;
import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Value
@RequiredArgsConstructor
public class EventQualificator {

    Map<ButtonClick, ScheduledFuture<?>> scheduledActionsMap = new HashMap<>();
    Map<ButtonClick, Integer> multiplicityMap = new HashMap<>();
    long longStep = 210;

    Sinks.Many<QualifiedEType> output;

    public void qualify(ButtonClick evt) {
        multiplicityMap.computeIfAbsent(evt, (k) -> 1);

        if (scheduledActionsMap.containsKey(evt)) {
            scheduledActionsMap.get(evt).cancel(true);

            scheduledActionsMap.remove(evt);
            multiplicityMap.remove(evt);

            output.tryEmitNext(QualifiedEType.builder()
                    .type(EType.valueOf(evt.getRelease().getName().toUpperCase()))
                    .multiplicity(EMultiplicity.DOUBLE)
                    .modifiers(convertModifiers(evt))
                    .longPress(computeIsLongPress(evt))
                    .build());

            return;
        }

        ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {

                    Integer pushes = multiplicityMap.get(evt);

                    output.tryEmitNext(QualifiedEType.builder()
                            .type(EType.valueOf(evt.getRelease().getName().toUpperCase()))
                            .multiplicity(EMultiplicity.CLICK)
                            .longPress(computeIsLongPress(evt))
                            .modifiers(convertModifiers(evt))
                            .build());

                    multiplicityMap.remove(evt);
                    scheduledActionsMap.remove(evt);

                }, 200, TimeUnit.MILLISECONDS);

        scheduledActionsMap.put(evt, future);
    }

    List<EType> convertModifiers(ButtonClick click) {
        return click.getModifiers().stream()
                .map(String::toUpperCase)
                .map(EType::valueOf)
                .toList();
    }

    boolean computeIsLongPress(ButtonClick tvPair) {
        return tvPair.getRelease().getTime() - tvPair.getPush().getTime() > longStep;
    }
}
