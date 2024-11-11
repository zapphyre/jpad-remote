package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import reactor.core.Disposable;

import static org.asmus.facade.TimedButtonGamepadFactory.createGamepadEventStream;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Disposable disposable = createGamepadEventStream()
                .map(Object::toString)
                .subscribe(log::info);

        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));
    }
}
