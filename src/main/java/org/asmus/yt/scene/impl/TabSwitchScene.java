package org.asmus.yt.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.yt.scene.Scene;

import static org.asmus.yt.xDoToolUtil.*;

@Slf4j
public class TabSwitchScene extends BaseScene {

    @Override
    public Scene rotaryCv() {
        keyRight();
        return this;
    }

    @Override
    public Scene rotaryCCV() {
        keyLeft();
        return this;
    }

    @Override
    public Scene rotaryClick() {
        pressSpace();
        tabSwitchOff();

        return new VideoPlayerScene();
    }
}
