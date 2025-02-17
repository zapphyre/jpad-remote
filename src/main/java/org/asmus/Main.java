package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.builder.OsConnector;


@Slf4j
public class Main {


    public static void main(String[] args) throws InterruptedException {
        OsConnector osConnector = new OsConnector();

        osConnector.watchForDevices( 0, 1);

        osConnector.getButtonStream()
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
