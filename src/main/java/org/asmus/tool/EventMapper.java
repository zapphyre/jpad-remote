package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;

import java.util.Map;
import java.util.function.Function;

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

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
