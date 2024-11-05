package org.asmus;

import org.asmus.service.JoyWorker;

public class Main {

    public static void main(String[] args) {
        JoyWorker joyWorker = new JoyWorker();

        joyWorker.hookOnJoy("/dev/input/js0");
    }
}
