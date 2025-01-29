package org.asmus.component;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.model.EMultiplicity;
import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;
import org.asmus.model.ButtonClick;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
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
    Duration longStep = Duration.ofMillis(210);

    Sinks.Many<QualifiedEType> output;

    public void qualify(ButtonClick evt) {
        multiplicityMap.computeIfAbsent(evt, (k) -> 1);

        if (scheduledActionsMap.containsKey(evt)) {
            scheduledActionsMap.get(evt).cancel(true);

            scheduledActionsMap.remove(evt);
            multiplicityMap.remove(evt);

            output.tryEmitNext(QualifiedEType.builder()
                    .type(translateBtn(evt))
                    .multiplicity(EMultiplicity.DOUBLE)
                    .modifiers(evt.getModifiers().stream()
                            .map(q -> EType.valueOf(q.getName().toUpperCase()))
                            .toList())
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
                            .modifiers(evt.getModifiers().stream()
                                    .map(q -> EType.valueOf(q.getName().toUpperCase()))
                                    .toList())
                            .build());


//                    scheduledActionsMap.keySet()
//                                    .removeIf(q -> evt.getModifiers().contains(q.getPush()));

                    multiplicityMap.remove(evt);
                    scheduledActionsMap.remove(evt);

                }, 200, TimeUnit.MILLISECONDS);

        scheduledActionsMap.put(evt, future);
    }

    boolean computeIsLongPress(ButtonClick tvPair) {
        return Duration.between(tvPair.getPush().getDate(), tvPair.getRelease().getDate())
                .compareTo(longStep) >= 1;
    }

    EType translateBtn(ButtonClick tvPair) {
        return switch (EButtonGamepadEvt.valueOf(tvPair.getRelease().getName().toUpperCase())) {
            case A -> EType.A;
            case B -> EType.B;
            case X -> EType.X;
            case Y -> EType.Y;
            case BUMPER_LEFT -> EType.BUMPER_LEFT;
            case BUMPER_RIGHT -> EType.BUMPER_RIGHT;
            case START -> EType.START;
            case SELECT -> EType.SELECT;
            case LEFT_STICK_CLICK -> EType.LEFT_STICK_CLICK;
        };
    }
}
