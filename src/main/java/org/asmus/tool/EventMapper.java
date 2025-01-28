package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.Gamepad;
import org.asmus.model.PolarCoords;

import java.util.function.Function;

@UtilityClass
public class EventMapper {

    public static Function<Gamepad, PolarCoords> translateAxis(Function<Gamepad, Integer> getterX,
                                                               Function<Gamepad, Integer> getterY) {
        return q -> {
            int yAxisLeft = getterY.apply(q);
            int xAxisLeft = getterX.apply(q);

            double theta = getTheta(xAxisLeft, yAxisLeft);
            double r = getR(xAxisLeft, yAxisLeft);

            return PolarCoords.builder()
                    .theta(theta)
                    .radius(r)
                    .build();
        };
    }

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
