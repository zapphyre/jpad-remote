package org.asmus.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPressType {
    CLICK(1),
    DOUBLE(2),
    TRIPLE(3),

    LONG(4),
    TOO_LONG(5),
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
