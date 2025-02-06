package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.ButtonNamePosition;
import org.asmus.model.NamingConstants;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public class SdlStringMapper {

    Predicate<String> hasColon = s -> s.contains(":");
    Predicate<String> analog = s -> s.contains("a");
    Predicate<String> button = s -> s.contains("b");

    Function<String, ButtonNamePosition> placeholder = q -> {
        String[] m = q.split(":");
        try {
            boolean axis = m[1].charAt(0) == 'a';
            int v = Integer.parseInt(m[1].substring(1));
            return new ButtonNamePosition(axis, m[0], v);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    public static List<ButtonNamePosition> translate(String input) {
        return Stream.of((input + "," + NamingConstants.ARROW_X + ":a6," + NamingConstants.ARROW_Y + ":a7").split(","))
                .filter(hasColon)
                .filter(analog.or(button))
                .map(placeholder)
                .filter(Objects::nonNull)
                .toList();
    }
}
