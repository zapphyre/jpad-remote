package org.asmus.yt.scene.impl;

import life.domacitempeh.mqttsensorcollector.scene.Scene;
import lombok.extern.slf4j.Slf4j;

import static life.domacitempeh.mqttsensorcollector.xDoToolUtil.*;

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
