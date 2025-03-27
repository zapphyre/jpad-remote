package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.EPolarDirection;
import org.asmus.model.PolarCoords;

import java.util.Map;
import java.util.function.Function;

import static org.asmus.model.EPolarDirection.*;

@UtilityClass
public class EventMapper {

    public static Function<Map<String, Integer>, PolarCoords> translateAxis(String x,
                                                                            String y) {
        return q -> {
            int yAxisLeft = q.get(y);
            int xAxisLeft = q.get(x);

            double theta = getTheta(xAxisLeft, yAxisLeft);
            double r = getR(xAxisLeft, yAxisLeft);

            return new PolarCoords(r, theta);
        };
    }

    int THRESHOLD = 2_000;

    public static EPolarDirection translateAxisMove(PolarCoords coords) {
        double theta = coords.theta();
        double r = coords.radius();

        if (theta == 0)
            return CENTER;

        if (r < THRESHOLD)
            return FIZZY;

        if (theta < 0.5 && theta > -0.5)
            return RIGHT;
        else if (theta < -0.5 && theta > -2.5)
            return LEFT;
        else if (theta > 0.5 && theta < 2.5)
            return DOWN;

        return LEFT;
    }

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
