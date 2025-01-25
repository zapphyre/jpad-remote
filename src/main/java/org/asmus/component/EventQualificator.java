package org.asmus.component;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.model.EMultiplicity;
import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TVPair;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Value
@RequiredArgsConstructor
public class EventQualificator {

    Map<TVPair, ScheduledFuture<?>> scheduledActionsMap = new HashMap<>();
    Map<TVPair, Integer> multiplicityMap = new HashMap<>();
    Duration longStep = Duration.ofMillis(210);

    Sinks.Many<QualifiedEType> output;

    public void qualify(TVPair evt) {
        multiplicityMap.computeIfPresent(evt, (k, v) -> v + 1);
        multiplicityMap.computeIfAbsent(evt, (k) -> 1);

        if (scheduledActionsMap.containsKey(evt)) {
            scheduledActionsMap.get(evt).cancel(true);
            scheduledActionsMap.remove(evt);
        }

        ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {

                    Integer pushes = multiplicityMap.get(evt);

                    output.tryEmitNext(QualifiedEType.builder()
                            .type(translateBtn(evt))
                            .multiplicity(EMultiplicity.getByClickCount(pushes))
                            .longPress(computeIsLongPress(evt))
                            .build());

                    multiplicityMap.remove(evt);
                    scheduledActionsMap.remove(evt);

                }, 200, TimeUnit.MILLISECONDS);

        scheduledActionsMap.put(evt, future);
    }

    boolean computeIsLongPress(TVPair tvPair) {
        return Duration.between(tvPair.getPush().getDate(), tvPair.getRelease().getDate())
                .compareTo(longStep) >= 1;
    }

    EType translateBtn(TVPair tvPair) {
        return switch (EButtonGamepadEvt.valueOf(tvPair.getRelease().getName().toUpperCase())) {
            case A -> EType.A;
            case B -> EType.B;
            case X -> EType.X;
            case Y -> EType.Y;
            case START -> EType.START;
            case SELECT -> EType.SELECT;
            case LEFT_STICK_CLICK -> EType.LEFT_STICK_CLICK;
        };
    }
}
