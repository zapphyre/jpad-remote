package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import org.asmus.yt.model.Gamepad;

import java.util.concurrent.Executors;

@Slf4j
public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();

        Executors.newSingleThreadScheduledExecutor()
                .execute(() -> joyWorker.hookOnJoy("/dev/input/js0"));

        joyWorker.getGamepadEvents()
                .map(Gamepad::toString)
                .subscribe(log::info);
    }
}
