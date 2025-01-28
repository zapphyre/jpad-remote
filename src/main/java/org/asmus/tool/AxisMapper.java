package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.EType;
import org.asmus.model.Gamepad;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TriggerPosition;

import java.util.function.Function;

@UtilityClass
public class AxisMapper {

    public static Function<Gamepad, QualifiedEType> mapVertical = q -> q.getVERTICAL_BTN() > 0 ?
            QualifiedEType.builder()
                    .type(EType.DOWN)
                    .build() : QualifiedEType.builder()
            .type(EType.UP)
            .build();

    public static Function<Gamepad, QualifiedEType> mapHorizontal = q -> q.getHORIZONTAL_BTN() > 0 ?
            QualifiedEType.builder()
                    .type(EType.RIGHT)
                    .build() : QualifiedEType.builder()
            .type(EType.LEFT)
            .build();

    public static Function<Gamepad, TriggerPosition> getTriggerPosition(Function<Gamepad, Integer> getter) {
        return q -> TriggerPosition.builder()
                .position(getter.apply(q))
                .build();
    }
}
