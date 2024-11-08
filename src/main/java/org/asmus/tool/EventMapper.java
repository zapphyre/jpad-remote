package org.asmus.tool;

import org.asmus.evt.EAxisGamepadEvt;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.model.EPressType;
import org.asmus.model.EType;
import org.asmus.model.MeteredKeyEvent;
import org.asmus.model.QualifiedEType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EventMapper {

    private static Duration longStep = Duration.ofMillis(300);
    private static Map<EType, MeteredKeyEvent> eventsMap = new EnumMap<>(EType.class);

    public static QualifiedEType translateTimed(List<GamepadIntrospector.TVPair> pairs) {
        QualifiedEType qualifiedEType = translate(pairs);

        if (qualifiedEType.getPressType().ordinal() > 2) return qualifiedEType;

        MeteredKeyEvent last = eventsMap.computeIfAbsent(qualifiedEType.getType(), q -> MeteredKeyEvent.builder()
                .build());
        LocalDateTime now = LocalDateTime.now();
        boolean multiclick = Duration.between(last.getLastEvent(), now).compareTo(longStep) < 0;

        if (!multiclick) {
            eventsMap.put(qualifiedEType.getType(), MeteredKeyEvent.builder()
                    .multiplicity(new AtomicInteger(1))
                    .build());

            return qualifiedEType;
        }

        AtomicInteger incremented = last.incrementAndGetMultiplicity();
        eventsMap.put(qualifiedEType.getType(), last
                .withMultiplicity(incremented)
                .withLastEvent(now));

        return qualifiedEType.withPressType(EPressType.getByClickCount(incremented.get()));
    }

    public static QualifiedEType translate(List<GamepadIntrospector.TVPair> pairs) {
        GamepadIntrospector.TVPair tvPair = pairs.getFirst();

        EType type = translate(tvPair);

        Duration between = Duration.between(tvPair.getFirst().getDate(), tvPair.getSecond().getDate());
        long holdPeriods = between.dividedBy(longStep);

        return QualifiedEType.builder()
                .type(type)
                .pressType(holdPeriods > 0 ? holdPeriods > 2 ? EPressType.TOO_LONG : EPressType.LONG : EPressType.CLICK)
                .build();
    }

    public static EType translate(GamepadIntrospector.TVPair tvPair) {
        boolean axisEvt = Arrays.stream(EAxisGamepadEvt.values())
                .anyMatch(q -> q.name().equals(tvPair.getFirst().getName()));

        if (axisEvt) {
            int firstAxis = Integer.parseInt(tvPair.getFirst().getValue());
            int secondAxis = Integer.parseInt(tvPair.getSecond().getValue());

            if (EAxisGamepadEvt.valueOf(tvPair.getFirst().getName()) == EAxisGamepadEvt.LEFT_STICK_X)
                if (firstAxis > secondAxis) {
                    return EType.LEFT_STICK_LEFT;
                } else {
                    return EType.LEFT_STICK_RIGHT;
                }

            if (EAxisGamepadEvt.valueOf(tvPair.getFirst().getName()) == EAxisGamepadEvt.LEFT_STICK_Y) {
                if (firstAxis > secondAxis) {
                    return EType.LEFT_STICK_UP;
                } else {
                    return EType.LEFT_STICK_DOWN;
                }
            }
        } else {
            return switch (EButtonGamepadEvt.valueOf(tvPair.getFirst().getName().toUpperCase())) {
                case A -> EType.A;
                case B -> EType.B;
                case X -> EType.X;
                case Y -> EType.Y;
            };
        }

        throw new RuntimeException();
    }
}
