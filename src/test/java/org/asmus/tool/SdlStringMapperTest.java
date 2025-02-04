package org.asmus.tool;

import org.asmus.model.ButtonNamePosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.asmus.tool.SdlStringMapper.translate;

public class SdlStringMapperTest {

    @Test
    void testSdlStringMapper() {
        String mapping = """
                0300b18cf00300008d04000001010000,HyperX Clutch,a:b0,b:b1,x:b2,y:b3,back:b6,guide:b8,start:b7,leftstick:b9,rightstick:b10,leftshoulder:b4,rightshoulder:b5,dpup:h0.1,dpdown:h0.4,dpleft:h0.8,dpright:h0.2,leftx:a0,lefty:a1,rightx:a3,righty:a4,lefttrigger:a2,righttrigger:a5,crc:8cb1,platform:Linux	
                """;

        List<ButtonNamePosition> translate = translate(mapping);

        List<ButtonNamePosition> axis = translate.stream().filter(ButtonNamePosition::axis).toList();
        List<ButtonNamePosition> buttons = translate.stream().filter(Predicate.not(ButtonNamePosition::axis)).toList();

        Assertions.assertEquals(8, axis.size());
        Assertions.assertEquals(11, buttons.size());
    }
}
