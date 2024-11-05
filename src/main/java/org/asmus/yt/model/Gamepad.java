package org.asmus.yt.model;

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

    int DPAD_UP;
    int DPAD_DOWN;
    int DPAD_LEFT;
    int DPAD_RIGHT;
    int DPAD_CENTER;

    boolean DPAD_LEFT_BUMPER;
    boolean DPAD_RIGHT_BUMPER;
    boolean DPAD_LEFT_TRIGGER;
    boolean DPAD_RIGHT_TRIGGER;
}

