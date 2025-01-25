package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.Gamepad;
import org.asmus.model.PolarCoords;
import org.asmus.model.StickMovement;

@UtilityClass
public class EventMapper {

    public static StickMovement translateAxis(Gamepad gamepad) {
        int yAxisLeft = gamepad.getLEFT_STICK_Y();
        int xAxisLeft = gamepad.getLEFT_STICK_X();

        double theta = getTheta(xAxisLeft, yAxisLeft);
        double r = getR(xAxisLeft, yAxisLeft);

        return StickMovement.builder()
                .left(PolarCoords.builder()
                        .theta(theta)
                        .radius(r)
                        .build())
                .build();
    }

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
