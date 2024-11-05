package org.asmus.yt.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Gamepad {
    private boolean A;
    private boolean B;
    private boolean X;
    private boolean Y;
    private boolean LEFT;
    private boolean RIGHT;
    private boolean UP;
    private boolean DOWN;
    private boolean LEFT_BUMPER;
    private boolean RIGHT_BUMPER;
    private boolean BACK;
    private boolean START;
    private int DPAD_UP;
    private int DPAD_DOWN;
    private int DPAD_LEFT;
    private int DPAD_RIGHT;
    private int DPAD_CENTER;

    private boolean DPAD_LEFT_BUMPER;
    private boolean DPAD_RIGHT_BUMPER;
    private boolean DPAD_LEFT_TRIGGER;
    private boolean DPAD_RIGHT_TRIGGER;
}
