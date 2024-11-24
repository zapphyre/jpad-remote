package org.asmus.tool;

import org.asmus.model.EPressType;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TVPair;
import org.asmus.model.TimedValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EventMapperTest {

    @Test
    void testClickMultiplicity() throws InterruptedException {
        TVPair first = TVPair.builder()
                .first(TimedValue.builder()
                        .name("a")
                        .build())
                .second(TimedValue.builder()
                        .name("B")
                        .build())
                .build();

        TVPair second = TVPair.builder()
                .first(TimedValue.builder()
                        .name("a")
                        .date(LocalDateTime.now().plus(50, ChronoUnit.MILLIS))
                        .build())
                .second(TimedValue.builder()
                        .name("x")
                        .build())
                .build();

        QualifiedEType qualifiedEType = EventMapper.translateButtonTimed(List.of(first, second));
        Assertions.assertEquals(EPressType.CLICK, qualifiedEType.getPressType());

        Thread.sleep(500);

        qualifiedEType = EventMapper.translateButtonTimed(List.of(first, second));
        Assertions.assertEquals(EPressType.CLICK, qualifiedEType.getPressType());

        qualifiedEType = EventMapper.translateButtonTimed(List.of(first, second));
        Assertions.assertEquals(EPressType.DOUBLE, qualifiedEType.getPressType());

        qualifiedEType = EventMapper.translateButtonTimed(List.of(first, second));
        Assertions.assertEquals(EPressType.TRIPLE, qualifiedEType.getPressType());
    }
}
