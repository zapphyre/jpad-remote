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

        timedButtonGamepadFactory.getButtonStream()
                .subscribe(System.out::println);

//        timedButtonGamepadFactory.getArrowsStream()
//                .subscribe(System.out::println);
//
//        timedButtonGamepadFactory.getTriggerStream()
//                .subscribe(System.out::println);
//
//        timedButtonGamepadFactory.getArrowsStream()
//                .subscribe(System.out::println);
    }


}
