package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.evt.EAxisGamepadEvt;
import org.asmus.evt.EButtonGamepadEvt;
import org.asmus.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class EventMapper {

    Duration longStep = Duration.ofMillis(300);
    Map<EType, MeteredKeyEvent> eventsMap = new EnumMap<>(EType.class);

    public static QualifiedEType translateTimed(List<TVPair> pairs) {
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

        EType type = translate(tvPair);

        Duration between = Duration.between(tvPair.getFirst().getDate(), tvPair.getSecond().getDate());
        long holdPeriods = between.dividedBy(longStep);

        return QualifiedEType.builder()
                .type(type)
                .pressType(holdPeriods > 0 ? holdPeriods > 2 ? EPressType.TOO_LONG : EPressType.LONG : EPressType.CLICK)
                .build();
    }

    static EType translate(TVPair tvPair) {
        boolean buttonEvt = Arrays.stream(EButtonGamepadEvt.values())
                .anyMatch(q -> q.name().equals(tvPair.getSecond().getName().toUpperCase()));

        if (buttonEvt)
            return switch (EButtonGamepadEvt.valueOf(tvPair.getSecond().getName().toUpperCase())) {
                case A -> EType.A;
                case B -> EType.B;
                case X -> EType.X;
                case Y -> EType.Y;
                case LEFT_STICK_CLICK -> EType.LEFT_STICK_CLICK;
            };

        EAxisGamepadEvt axisEvt = EAxisGamepadEvt.valueOf(tvPair.getSecond().getName());

        int yAxis = 0;
        if (axisEvt == EAxisGamepadEvt.LEFT_STICK_Y)
            yAxis = Integer.parseInt(tvPair.getSecond().getValue());

        int xAxis = 0;
        if (axisEvt == EAxisGamepadEvt.LEFT_STICK_X)
            xAxis = Integer.parseInt(tvPair.getSecond().getValue());

        double theta = getTheta(xAxis, yAxis);
        double r = getR(xAxis, yAxis);
//        System.out.println("thta: " + theta);
//        System.out.println("r: " + r);

//        if (xAxis == 0 || yAxis == 0)
//            return EType.FIZZY;

        final int trshold = 2_000;

        if (r < trshold)
            return EType.FIZZY;

        if (theta < 0.5 && theta > -0.5)
            return EType.LEFT_STICK_RIGHT;
        else if (theta < 0.5 && theta > -2.5)
            return EType.LEFT_STICK_UP;
         else if (theta > 0.5 && theta < 2.5)
            return EType.LEFT_STICK_DOWN;
        else
            return EType.LEFT_STICK_LEFT;
    }

    public static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    public static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }

    static boolean axisWithinBounds(int axis) {
        System.out.println("xAxis: " + axis);
        return Math.abs(axis) < 5_000;
    }
}
