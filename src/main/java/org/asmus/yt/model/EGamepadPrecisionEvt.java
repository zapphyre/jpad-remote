package org.asmus.yt.model;

import lombok.Getter;

@Getter
public enum EGamepadPrecisionEvt {
    L_STICK_LEFT(9),
    L_STICK_RIGHT(9),
    ;

    final int num;

    EGamepadPrecisionEvt(int num) {
        this.num = num;
    }
}
