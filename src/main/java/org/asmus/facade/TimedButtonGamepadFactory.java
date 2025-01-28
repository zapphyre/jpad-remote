package org.asmus.facade;

import lombok.experimental.UtilityClass;
import org.asmus.component.EventQualificator;
import org.asmus.model.*;
import org.asmus.service.JoyWorker;
import org.asmus.tool.AxisMapper;
import org.asmus.tool.EventMapper;
import org.asmus.tool.GamepadIntrospector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@UtilityClass
public class TimedButtonGamepadFactory {

    GamepadStateStream stateStream = new JoyWorker().hookOnDefault();

    public static Flux<QualifiedEType> getButtonStream() {
        Sinks.Many<QualifiedEType> out = Sinks.many().multicast().directBestEffort();
        EventQualificator eventQualificator = new EventQualificator(out);

        stateStream.getButtonFlux()
                .mapNotNull(GamepadIntrospector::introspect)
                .subscribe(eventQualificator::qualify);

        return out.asFlux();
    }

    public static Flux<QualifiedEType> getArrowsStream() {
        Flux<QualifiedEType> vertical = stateStream.getAxisFlux()
                .filter(q -> q.getVERTICAL_BTN() != 0)
                .map(AxisMapper.mapVertical);

        Flux<QualifiedEType> horizontal = stateStream.getAxisFlux()
                .filter(q -> q.getHORIZONTAL_BTN() != 0)
                .map(AxisMapper.mapHorizontal);

        return Flux.merge(vertical, horizontal)
                .publish().autoConnect();
    }

    public static Flux<TriggerPosition> getTriggerStream() {
        Flux<TriggerPosition> left = stateStream.getAxisFlux()
                .distinctUntilChanged(Gamepad::getTRIGGER_LEFT)
                .map(AxisMapper.getTriggerPosition(Gamepad::getTRIGGER_LEFT))
                .map(q -> q.withType(EType.TRIGGER_LEFT));

        Flux<TriggerPosition> right = stateStream.getAxisFlux()
                .distinctUntilChanged(Gamepad::getTRIGGER_RIGHT)
                .map(AxisMapper.getTriggerPosition(Gamepad::getTRIGGER_RIGHT))
                .map(q -> q.withType(EType.TRIGGER_RIGHT));

        return Flux.merge(left, right).publish().autoConnect();
    }

    public static Flux<PolarCoords> getLeftStickStream() {
        return stateStream.getAxisFlux()
                .distinctUntilChanged(Gamepad::getLEFT_STICK_X)
                .distinctUntilChanged(Gamepad::getLEFT_STICK_Y)
                .map(EventMapper.translateAxis(Gamepad::getLEFT_STICK_X, Gamepad::getLEFT_STICK_Y))
                .map(q -> q.withType(EType.LEFT_STICK_MOVE));
    }

    public static Flux<PolarCoords> getRightStickStream() {
        return stateStream.getAxisFlux()
                .distinctUntilChanged(Gamepad::getLEFT_STICK_X)
                .distinctUntilChanged(Gamepad::getLEFT_STICK_Y)
                .map(EventMapper.translateAxis(Gamepad::getRIGHT_STICK_X, Gamepad::getRIGHT_STICK_Y))
                .map(q -> q.withType(EType.RIGHT_STICK_MOVE));
    }
}
