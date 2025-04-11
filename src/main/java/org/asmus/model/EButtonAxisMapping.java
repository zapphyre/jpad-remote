package org.asmus.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EButtonAxisMapping {

    A("a", false),
    B("b", false),
    X("x", false),
    Y("y", false),

    SELECT("back", false),
    START("start", false),

    LEFT_STICK_CLICK("leftstick", false),
    RIGHT_STICK_CLICK("rightstick", false),

    BUMPER_LEFT("leftshoulder", false),
    BUMPER_RIGHT("rightshoulder", false),

    OTHER("other", false),

    UP("dpup", false),
    DOWN("dpdown", false),
    LEFT("dpleft", false),
    RIGHT("dpright", false),

    LEFT_STICK_X(NamingConstants.LEFT_STICK_X, true),
    LEFT_STICK_Y(NamingConstants.LEFT_STICK_Y, true),
    RIGHT_STICK_X(NamingConstants.RIGHT_STICK_X, true),
    RIGHT_STICK_Y(NamingConstants.RIGHT_STICK_Y, true),

    TRIGGER_LEFT(NamingConstants.LEFT_TRIGGER, true),
    TRIGGER_RIGHT(NamingConstants.RIGHT_TRIGGER, true),

    ;

    final String mapping;
    final boolean analog;

    public static EButtonAxisMapping getByEnumName(String name) {
        for (EButtonAxisMapping mapping : values()) {
            if (mapping.name().equalsIgnoreCase(name)) {
                return mapping;
            }
        }

        throw new IllegalArgumentException("Unknown button axis " + name);
    }

    public static EButtonAxisMapping getByMappingName(final String name) {
        for (EButtonAxisMapping value : EButtonAxisMapping.values()) {
            if (value.mapping.equals(name))
                return value;
        }

        return OTHER;
    }

    public static String getInternalByEnumName(String name) {
        for (EButtonAxisMapping value : EButtonAxisMapping.values()) {
            if (value.name().equals(name))
                return value.mapping;
        }

        return OTHER.mapping;
    }
}
