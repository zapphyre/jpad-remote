package org.asmus.component;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.*;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Value
@RequiredArgsConstructor
public class EventQualificator {

    Map<ButtonClick, ScheduledFuture<?>> scheduledActionsMap = new HashMap<>();
    long longStep = 210;

    Sinks.Many<GamepadEvent> output;

    public void qualify(ButtonClick evt) {
        if (scheduledActionsMap.containsKey(evt)) {
            scheduledActionsMap.get(evt).cancel(true);

            output.tryEmitNext(toGamepadEventWith(EMultiplicity.DOUBLE).apply(evt));

            scheduledActionsMap.remove(evt);
            return;
        }

        ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            output.tryEmitNext(toGamepadEventWith(EMultiplicity.CLICK).apply(evt));

            scheduledActionsMap.remove(evt);
        }, 200, TimeUnit.MILLISECONDS);

        scheduledActionsMap.put(evt, future);
    }

    Function<ButtonClick, GamepadEvent> toGamepadEventWith(EMultiplicity multiplicity) {
        return q -> GamepadEvent.builder()
                .type(EButtonAxisMapping.getByName(q.getRelease().getName()))
                .multiplicity(multiplicity)
                .longPress(computeIsLongPress(q))
                .modifiers(convertModifiers(q))
                .eventName(q.getRelease().getName())
                .build();
    }

    List<EButtonAxisMapping> convertModifiers(ButtonClick click) {
        return click.getModifiers().stream()
                .map(EButtonAxisMapping::getByName)
                .toList();
    }

    boolean computeIsLongPress(ButtonClick tvPair) {
        return tvPair.getRelease().getTime() - tvPair.getPush().getTime() > longStep;
    }
}
