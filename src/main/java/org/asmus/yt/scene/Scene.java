package org.asmus.yt.scene;

public interface Scene {

    Scene rotaryCv();

    Scene rotaryCCV();

    Scene rotaryClick();

    Scene leftStickClick();

    Scene buttonRed();

    Scene buttonRedLong();

    Scene buttonGreen();

    Scene buttonGreenLong();

    Scene buttonA();

    Scene buttonB();

    Scene rotaryDouble();

    Scene goBack();

    void setPrevious(Scene previous);
}
