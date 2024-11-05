package org.asmus.yt.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.yt.scene.Scene;

import static org.asmus.yt.xDoToolUtil.*;

@Slf4j
public class BaseScene implements Scene {

    private Scene previous;
    private boolean back;

    @Override
    public Scene rotaryCv() {
        pageUp();

        return this;
    }

    @Override
    public Scene rotaryCCV() {
        pageDown();

        return this;
    }

    @Override
    public Scene rotaryClick() {
        click();

        return this;
    }

    public Scene leftStickClick() {
        tabSwitchOn();

        return new TabSwitchScene();
    }

    @Override
    public Scene buttonRed() {
        return this;
    }

    @Override
    public Scene buttonRedLong() {
        return this;
    }

    @Override
    public Scene buttonGreen() {
        log.debug("base green click");

        pressCtrlT();

        return new NewTabScene();
    }

    @Override
    public Scene buttonGreenLong() {
        log.debug("base green long click");

        pressCtrlW();

        return new NewTabScene();
    }

    public Scene buttonA() {
        log.debug("base yellow click");

        togglePlayYoutube();

        return this;
    }

    public Scene buttonB() {
        pressF();

        return new FullscreenScene();
    }

    @Override
    public Scene rotaryDouble() {

        return previous;
    }

    @Override
    public Scene goBack() {
        if (back = !back)
            ffBack();
        else
            ffFwd();

        return previous;
    }

    @Override
    public void setPrevious(Scene previous) {
        this.previous = previous;
    }
}
