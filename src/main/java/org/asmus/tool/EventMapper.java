package org.asmus.tool;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.asmus.model.Gamepad;
import org.asmus.model.PolarCoords;
import org.asmus.model.StickMovement;

import java.util.function.Function;

@Value
@RequiredArgsConstructor
public class EventMapper {

    Function<Gamepad, Integer> xGetter;
    Function<Gamepad, Integer> yGetter;

    public PolarCoords translateAxis(Gamepad gamepad) {
        int yAxisLeft = yGetter.apply(gamepad);
        int xAxisLeft = xGetter.apply(gamepad);

        double theta = getTheta(xAxisLeft, yAxisLeft);
        double r = getR(xAxisLeft, yAxisLeft);

        return PolarCoords.builder()
                .theta(theta)
                .radius(r)
                .build();
    }

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
