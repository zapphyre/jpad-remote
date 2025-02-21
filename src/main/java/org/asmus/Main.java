package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.GamepadEventSourceBuilder;
import org.asmus.builder.EventProducer;
import org.asmus.builder.closure.OsDevice;
import org.asmus.builder.closure.RawArrowSource;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.BehavioralFilter;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.qualifier.impl.ModifierAndLongPressQualifier;

import java.util.Arrays;

import static org.asmus.builder.GamepadEventSourceBuilder.LONG;


@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        EventProducer eventProducer = new EventProducer();

        eventProducer.watchForDevices(0, 1);
        ActuationBehaviour behaviour = ActuationBehaviour.builder()
                .introspector(new ReleaseIntrospector())
                .qualifier(new ModifierAndLongPressQualifier())
                .build();

        GamepadEventSourceBuilder gamepadEventSourceBuilder = new GamepadEventSourceBuilder();

        OsDevice wrapper = gamepadEventSourceBuilder.getButtonStream()
                .act(q -> BehavioralFilter.builder()
                        .behaviour(LONG)
                        .buttons(Arrays.stream(EButtonAxisMapping.values()).toList())
                        .build());

        eventProducer.getWorker().getButtonStream()
                .map(q -> q)
                .subscribe(wrapper::processButtonEvents);
//
        gamepadEventSourceBuilder.getQualifiedEventStream().asFlux()
                .log()
                .subscribe();

        RawArrowSource arrowsStream = gamepadEventSourceBuilder.getArrowsStream(eventProducer.getWorker().getButtonStream());
        eventProducer.getWorker().getAxisStream()
                .subscribe(arrowsStream::processArrowEvents);

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
