package org.asmus.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EMultiplicity {
    CLICK(1),
    DOUBLE(2),
    TRIPLE(3),
    MULTIPLE(9)
    ;

    final int clickCount;

    static public EMultiplicity getByClickCount(int clickCount) {
        return switch (clickCount) {
            case 1 -> CLICK;
            case 2 -> DOUBLE;
            case 3 -> TRIPLE;
            default -> MULTIPLE;
        };
    }
}
