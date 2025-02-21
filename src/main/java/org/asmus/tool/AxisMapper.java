package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.*;

import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class AxisMapper {

    public static Function<Map.Entry<String, Integer>, GamepadEvent> mapVertical =
            q -> q.getKey().equals(NamingConstants.ARROW_Y) && q.getValue() > 0 ?
            GamepadEvent.builder()
                    .type(EButtonAxisMapping.DOWN)
                    .build() : GamepadEvent.builder()
            .type(EButtonAxisMapping.UP)
            .build();

    public static Function<Map.Entry<String, Integer>, GamepadEvent> mapHorizontal =
            q -> q.getKey().equals(NamingConstants.ARROW_X) && q.getValue() > 0 ?
            GamepadEvent.builder()
                    .type(EButtonAxisMapping.RIGHT)
                    .build() : GamepadEvent.builder()
            .type(EButtonAxisMapping.LEFT)
            .build();

    public static Function<Map<String, Integer>, TriggerPosition> getTriggerPosition(String axisName) {
        return q -> TriggerPosition.builder()
                .position(q.get(axisName))
                .build();
    }
}
