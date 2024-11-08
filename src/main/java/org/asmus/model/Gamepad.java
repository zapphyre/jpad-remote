package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class Gamepad {
    boolean A;
    boolean B;
    boolean X;
    boolean Y;

    boolean LEFT;
    boolean RIGHT;
    boolean UP;
    boolean DOWN;

    boolean LEFT_BUMPER;
    boolean RIGHT_BUMPER;

    boolean BACK;
    boolean START;

    int LEFT_STICK_Y;
    int LEFT_STICK_X;
//    int LEFT_STICK_LEFT;
//    int LEFT_STICK_RIGHT;

    int RIGHT_STICK_UP;
    int RIGHT_STICK_DOWN;
    int RIGHT_STICK_LEFT;
    int RIGHT_STICK_RIGHT;

    boolean DPAD_LEFT_BUMPER;
    boolean DPAD_RIGHT_BUMPER;
    boolean DPAD_LEFT_TRIGGER;
    boolean DPAD_RIGHT_TRIGGER;
}

