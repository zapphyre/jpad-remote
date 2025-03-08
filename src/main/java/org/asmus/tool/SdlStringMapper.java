package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.ButtonNamePosition;
import org.asmus.model.NamingConstants;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@UtilityClass
public class SdlStringMapper {

    Predicate<String> hasColon = s -> s.contains(":");
    Predicate<String> analog = s -> s.contains("a");
    Predicate<String> button = s -> s.contains("b");
    Predicate<String> arrows = s -> s.contains("dp");

    Function<String, ButtonNamePosition> placeholder = q -> {
        String[] m = q.split(":");
        try {
            String buttonName = m[0];
            String buttonPositionNum = m[1];
            boolean axis = buttonPositionNum.charAt(0) == 'a';
            boolean hat = false;

            int position = -1;
            if (buttonName.contains("dp")) {
                hat = buttonPositionNum.contains("h") || axis;
            } else {
                position = Integer.parseInt(buttonPositionNum.substring(1));
            }

            return ButtonNamePosition.builder()
                    .axis(axis)
                    .hat(hat)
                    .position(position)
                    .buttonName(buttonName)
                    .buttonCode(buttonPositionNum)
                    .build();
        } catch (NumberFormatException e) {
            return null;
        }
    };

    public static List<ButtonNamePosition> translate(String input) {
        List<ButtonNamePosition> translated = Stream.of(input.split(","))
                .filter(hasColon)
                .filter(analog.or(button).or(arrows))
                .map(placeholder)
                .filter(Objects::nonNull)
                .toList();

        Map<String, ButtonNamePosition> hats = translated.stream()
                .filter(ButtonNamePosition::isHat)
                .collect(toMap(ButtonNamePosition::getButtonName, Function.identity()));

        if (hats.isEmpty()) return translated;

        int axisCnt = (int) translated.stream()
                .filter(ButtonNamePosition::isAxis)
                .count();

        AtomicInteger ai = new AtomicInteger(axisCnt - 1);

        LinkedList<ButtonNamePosition> modifiable = new LinkedList<>(translated);

        axisPair.forEach((key, value) -> {
            ButtonNamePosition first = hats.remove(key);
            ButtonNamePosition second = hats.remove(value);

            modifiable.remove(first);
            modifiable.remove(second);

            modifiable.add(first.withPosition(ai.incrementAndGet()));
            modifiable.add(second.withPosition(ai.get()));
        });

        return modifiable;
    }

    Map<String, String> axisPair = new LinkedHashMap<>() {{
        put("dpright", "dpleft");
        put("dpup", "dpdown");
    }};
}
