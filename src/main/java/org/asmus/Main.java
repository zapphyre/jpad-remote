package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.QualifiedEType;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import static org.asmus.facade.TimedButtonGamepadFactory.getArrowsStream;
import static org.asmus.facade.TimedButtonGamepadFactory.getButtonStream;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Flux<QualifiedEType> publish = getButtonStream()
                .publish()
                .autoConnect()
                ;

        Disposable disposable = publish
                .log()
                .subscribe();
//
        getArrowsStream()
                .subscribe(System.out::println);
//
//        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));
    }
}
