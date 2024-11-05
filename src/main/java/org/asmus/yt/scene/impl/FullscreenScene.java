package org.asmus.yt.scene.impl;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FullscreenScene extends VideoPlayerScene {
    private EFullscreenRotaryFunction rotaryFunction = EFullscreenRotaryFunction.SEEK;

    @Override
    public Scene rotaryDouble() {
        rotaryFunction = rotaryFunction.next();

        return this;
    }

    @Override
    public Scene rotaryCv() {
        switch (rotaryFunction) {
            case VOL -> keyUp();
            case SEEK -> keyRight();
            case SCROLL -> pageUp();
            default -> keyUp();
        }

        return this;
    }

    @Override
    public Scene rotaryCCV() {
        switch (rotaryFunction) {
            case VOL -> keyDown();
            case SEEK -> keyLeft();
            case SCROLL -> pageDown();
            default -> keyDown();
        }

        return this;
    }
}
