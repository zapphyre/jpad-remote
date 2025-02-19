package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.OsConnector;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.qualifier.impl.ModifierAndLongPressQualifier;
import org.asmus.qualifier.impl.MultiplicityQualifier;

import java.util.Arrays;

import static org.asmus.builder.GamepadEventSourceBuilder.getButtonStream;


@Slf4j
public class Main {


    public static void main(String[] args) throws InterruptedException {
        OsConnector osConnector = new OsConnector();

        osConnector.watchForDevices( 0, 1);

        getButtonStream().device(osConnector.getWorker())
//                .actuation(RELEASE)
                .actuation(ActuationBehaviour.builder()
                        .introspector(new ReleaseIntrospector())
                        .qualifier(new ModifierAndLongPressQualifier())
//                        .qualifier(new MultiplicityQualifier())
                        .build())
                .buttons(Arrays.stream(EButtonAxisMapping.values()).toList())
                .subscribe(q -> log.info("{}", q));

//        osConnector.getButtonStream()
//                .subscribe(System.out::println);

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
