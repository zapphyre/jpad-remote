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

    int VERTICAL_BTN;
    int HORIZONTAL_BTN;

    boolean BUMPER_LEFT;
    boolean BUMPER_RIGHT;

    int TRIGGER_LEFT;
    int TRIGGER_RIGHT;

    boolean BACK;
    boolean START;
    boolean SELECT;

    int LEFT_STICK_Y;
    int LEFT_STICK_X;
    boolean LEFT_STICK_CLICK;

    int RIGHT_STICK_X;
    int RIGHT_STICK_Y;
    int RIGHT_STICK_CLICK;
}

