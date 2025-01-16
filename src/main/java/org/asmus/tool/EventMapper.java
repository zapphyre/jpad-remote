package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class EventMapper {

    int THRESHOLD = 2_000;
    Duration longStep = Duration.ofMillis(300);
    Map<EType, MeteredKeyEvent> eventsMap = new EnumMap<>(EType.class);

    public static QualifiedEType translateButtonTimed(List<TVPair> pairs) {
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

        AtomicInteger incremented = last.getAndIncrementMultiplicity();
        eventsMap.put(qualifiedEType.getType(), last
                .withMultiplicity(incremented)
                .withLastEvent(now));

        return qualifiedEType.withPressType(EPressType.getByClickCount(incremented.get()));
    }

    public static QualifiedEType translate(List<TVPair> pairs) {
        TVPair tvPair = pairs.getLast();

        EType type = translateBtn(tvPair);

        Duration between = Duration.between(tvPair.getPush().getDate(), tvPair.getRelease().getDate());
        long holdPeriods = between.dividedBy(longStep);

        return QualifiedEType.builder()
                .type(type)
                .pressType(holdPeriods > 0 ? holdPeriods > 2 ? EPressType.TOO_LONG : EPressType.LONG : EPressType.CLICK)
                .build();
    }

    static EType translateBtn(TVPair tvPair) {
        return switch (EButtonGamepadEvt.valueOf(tvPair.getRelease().getName().toUpperCase())) {
            case A -> EType.A;
            case B -> EType.B;
            case X -> EType.X;
            case Y -> EType.Y;
            case LEFT_STICK_CLICK -> EType.LEFT_STICK_CLICK;
        };
    }

    public static QualifiedEType translateAxis(Gamepad gamepad) {
        return QualifiedEType.builder()
                .type(translateAxisMove(gamepad))
                .pressType(EPressType.ANALOG)
                .build();
    }

    static EType translateAxisMove(Gamepad gamepad) {
        int yAxisLeft = gamepad.getLEFT_STICK_Y();
        int xAxisLeft = gamepad.getLEFT_STICK_X();

        double theta = getTheta(xAxisLeft, yAxisLeft);
        double r = getR(xAxisLeft, yAxisLeft);

        System.out.println("r: " + r);

        if (theta == 0)
            return EType.LEFT_STICK_CENTER;

//        System.out.println("x: " + xAxisLeft + " y: " + yAxisLeft + " theta: " + theta + " r: " + r);

        if (r < THRESHOLD)
            return EType.FIZZY;

        if (theta < 0.5 && theta > -0.5)
            return EType.LEFT_STICK_RIGHT;
        else if (theta < -0.5 && theta > -2.5)
            return EType.LEFT_STICK_UP;
        else if (theta > 0.5 && theta < 2.5)
            return EType.LEFT_STICK_DOWN;

        return EType.LEFT_STICK_LEFT;
    }

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }

    static boolean axisWithinBounds(int axis) {
        return Math.abs(axis) < 5_000;
    }
}
