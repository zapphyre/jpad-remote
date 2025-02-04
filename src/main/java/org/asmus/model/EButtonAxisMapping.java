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

    LEFT_STICK_X(NamingConstants.LEFT_STICK_X, true),
    LEFT_STICK_Y(NamingConstants.LEFT_STICK_Y, true),
    RIGHT_STICK_X(NamingConstants.RIGHT_STICK_X, true),
    RIGHT_STICK_Y(NamingConstants.RIGHT_STICK_Y, true),

    UP("dpup", false),
    DOWN("dpdown", false),
    LEFT("dpleft", false),
    RIGHT("dpright", false),

    TRIGGER_LEFT(NamingConstants.LEFT_TRIGGER, true),
    TRIGGER_RIGHT(NamingConstants.RIGHT_TRIGGER, true),

    BUMPER_LEFT("leftshoulder", false),
    BUMPER_RIGHT("rightshoulder", false),

    OTHER("other", false),
    ;

    final String str;
    final boolean analog;

    public static EButtonAxisMapping getByName(final String name) {
        for (EButtonAxisMapping value : EButtonAxisMapping.values()) {
            if (value.str.equals(name))
                return value;
        }

        return OTHER;
    }
}
