package org.asmus;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

import static org.asmus.facade.TimedButtonGamepadFactory.*;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Disposable disposable = getButtonStream()
                .map(Object::toString)
                .subscribe(log::info);

        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));
    }
}
