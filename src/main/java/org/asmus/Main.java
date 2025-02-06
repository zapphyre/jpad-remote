package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.asmus.model.GamepadEvent;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;


@Slf4j
public class Main {


    public static void main(String[] args) throws InterruptedException {
        TimedButtonGamepadFactory timedButtonGamepadFactory = new TimedButtonGamepadFactory();

        timedButtonGamepadFactory.watchForDevices( 0, 1);

        Flux<GamepadEvent> publish = timedButtonGamepadFactory.getButtonStream()
                .publish()
                .autoConnect();

        Disposable disposable = publish
                .log()
                .subscribe();

        timedButtonGamepadFactory.getArrowsStream()
                .subscribe(System.out::println);
    }


}
