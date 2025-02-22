package org.asmus.builder;

import lombok.experimental.UtilityClass;
import org.asmus.builder.closure.axis.PolarCoordsProducer;
import org.asmus.builder.closure.axis.TriggerPosProducer;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.NamingConstants;
import org.asmus.model.TriggerPosition;
import org.asmus.tool.AxisMapper;
import org.asmus.tool.EventMapper;

import java.util.Map;
import java.util.function.Predicate;

@UtilityClass
public class AxisEventFactory {

    public PolarCoordsProducer leftStickStream() {
        return q -> q.getAxisStream()
                .filter(notZeroFor(NamingConstants.LEFT_STICK_X).or(notZeroFor(NamingConstants.LEFT_STICK_Y)))
                .map(EventMapper.translateAxis(NamingConstants.LEFT_STICK_X, NamingConstants.LEFT_STICK_Y));
    }

    public PolarCoordsProducer rightStickStream() {
        return q -> q.getAxisStream()
                .filter(notZeroFor(NamingConstants.RIGHT_STICK_X).or(notZeroFor(NamingConstants.RIGHT_STICK_Y)))
                .map(EventMapper.translateAxis(NamingConstants.RIGHT_STICK_X, NamingConstants.RIGHT_STICK_Y));
    }

    public TriggerPosProducer rightTriggerStream() {
        return q -> q.getAxisStream()
                .map(AxisMapper.getTriggerPosition(NamingConstants.RIGHT_TRIGGER))
                .filter(triggerEngaged)
                .map(p -> p.withType(EButtonAxisMapping.TRIGGER_RIGHT));
    }

    public TriggerPosProducer leftTriggerStream() {
        return q -> q.getAxisStream()
                .map(AxisMapper.getTriggerPosition(NamingConstants.LEFT_TRIGGER))
                .filter(triggerEngaged)
                .map(p -> p.withType(EButtonAxisMapping.TRIGGER_LEFT));
    }

    static Predicate<TriggerPosition> triggerEngaged = q -> q.getPosition() != -32767;

    static Predicate<Map<String, Integer>> notZeroFor(String axisName) {
        return q -> q.get(axisName) != 0;
    }}
