package org.asmus.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPressType {
    CLICK(1),
    DOUBLE(2),
    TRIPLE(3),

    ANALOG(4),

    LONG(5),
    TOO_LONG(6),
    ;

    final int clickCount;

    static public EPressType getByClickCount(int clickCount) {
        return switch (clickCount) {
            case 1 -> CLICK;
            case 2 -> DOUBLE;
            case 3 -> TRIPLE;
            default -> LONG;
        };
    }
}
