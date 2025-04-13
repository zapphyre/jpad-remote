package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.EventProducer;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.EQualificationType;
import org.asmus.qualifier.impl.MultiplicityQualifier;


@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        EventProducer eventProducer = new EventProducer();

        eventProducer.watchForDevices(0, 1);
        ActuationBehaviour behaviour = ActuationBehaviour.builder()
                .introspector(new ReleaseIntrospector())
                .qualifier(new MultiplicityQualifier())
                .build();

        IntrospectedEventFactory gamepadEventSourceBuilder = new IntrospectedEventFactory();

        OsDevice buttonProcessor = gamepadEventSourceBuilder.getButtonStream();
        RawArrowSource arrowsStream = gamepadEventSourceBuilder.getArrowsStream();
        RawArrowSource triggerStream = gamepadEventSourceBuilder.rightTriggerStream();
        RawArrowSource triggerLeft = gamepadEventSourceBuilder.leftTriggerStream();
        RawArrowSource triggerRangeDigi = gamepadEventSourceBuilder.leftDigitizedRangeTriggerStream();

        eventProducer.getWorker().getButtonStream()
                .subscribe(buttonProcessor::processButtonEvents);

        eventProducer.getWorker().getAxisStream()
                .subscribe(arrowsStream::processArrowEvents);

        eventProducer.getWorker().getAxisStream()
                        .subscribe(triggerStream::processArrowEvents);

        eventProducer.getWorker().getAxisStream()
                        .subscribe(triggerRangeDigi::processArrowEvents);

        // subscribe to all events
        gamepadEventSourceBuilder.getButtonEventStream()
                .log()
                .subscribe();

        eventProducer.getWorker().getAxisStream()
                .subscribe(gamepadEventSourceBuilder.leftStickStream()::processArrowEvents);
        eventProducer.getWorker().getAxisStream()
                .subscribe(gamepadEventSourceBuilder.rightStickStream()::processArrowEvents);

//        osConnector.getButtonStream()
//                .subscribe(System.out::println);

    }
}
