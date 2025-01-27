package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.EType;
import org.asmus.model.Gamepad;
import org.asmus.model.QualifiedEType;

import java.util.function.Function;

@UtilityClass
public class AxisButtonMapper {

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
}
