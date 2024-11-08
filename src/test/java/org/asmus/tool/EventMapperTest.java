package org.asmus.tool;

import org.asmus.model.QualifiedEType;
import org.asmus.model.TimedValue;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EventMapperTest {

    @Test
    void testClickMultiplicity() throws InterruptedException {
        GamepadIntrospector.TVPair first = GamepadIntrospector.TVPair.builder()
                .first(TimedValue.builder()
                        .name("a")
                        .build())
                .second(TimedValue.builder()
                        .name("B")
                        .build())
                .build();

        GamepadIntrospector.TVPair second = GamepadIntrospector.TVPair.builder()
                .first(TimedValue.builder()
                        .name("a")
                        .date(LocalDateTime.now().plus(50, ChronoUnit.MILLIS))
                        .build())
                .second(TimedValue.builder()
                        .name("x")
                        .build())
                .build();

        QualifiedEType qualifiedEType = EventMapper.translateTimed(List.of(first, second));
        System.out.println(qualifiedEType);

        Thread.sleep(500);
        qualifiedEType = EventMapper.translateTimed(List.of(first, second));
        System.out.println(qualifiedEType);
        qualifiedEType = EventMapper.translateTimed(List.of(first, second));
        System.out.println(qualifiedEType);
        qualifiedEType = EventMapper.translateTimed(List.of(first, second));
        System.out.println(qualifiedEType);
    }
}