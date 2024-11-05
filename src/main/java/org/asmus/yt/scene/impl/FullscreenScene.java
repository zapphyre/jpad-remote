package org.asmus.yt.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.yt.scene.Scene;


@Slf4j
public class FullscreenScene extends VideoPlayerScene {

    @Override
    public Scene rotaryDouble() {

        return this;
    }

    @Override
    public Scene rotaryCv() {

        return this;
    }

    @Override
    public Scene rotaryCCV() {

        return this;
    }
}
