package org.asmus.yt.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.yt.scene.Scene;

@Slf4j
public class VideoPlayerScene extends BaseScene {

    @Override
    public Scene leftStickClick() {
        pressF();

        return new FullscreenScene();
    }


}