package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.ELogicalEventType;
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

    public static Function<PolarCoords, ELogicalEventType> translateAxisMove = coords -> {
        double theta = coords.theta();
        double r = coords.radius();

//        System.out.println("theta: " + theta + ", radius: " + r);

        if (theta == 0) {
            return ELogicalEventType.CENTER;
        }

        if (r < THRESHOLD) {
            return ELogicalEventType.CENTER;
        }

        if (theta >= -0.785 && theta < 0.785) {
            return ELogicalEventType.RIGHT;
        } else if (theta >= 0.785 && theta < 2.356) {
            return ELogicalEventType.DOWN;
        } else if (theta >= -2.356 && theta < -0.785) {
            return ELogicalEventType.UP;
        }

        return ELogicalEventType.LEFT;
    };

    public static abstract class Heading {
//        public abstract Heading nextHeading(Heading prev);
//        public abstract EPolarDirection getHeading();
    }

    public static class Noop extends Heading {
//        @Override
        public Heading nextHeading(Heading prev) {
            return this;
        }

//        @Override
        public EPolarDirection getHeading() {
            return FIZZY;
        }
    }
    public static class Up extends Heading {}
    public static class Down extends Heading {}
    public static class Left extends Heading {}
    public static class Right extends Heading {}
    public static class Center extends Heading {}

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
