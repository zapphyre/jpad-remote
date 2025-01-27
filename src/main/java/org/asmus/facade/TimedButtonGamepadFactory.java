package org.asmus.facade;

import lombok.experimental.UtilityClass;
import org.asmus.component.EventQualificator;
import org.asmus.model.Gamepad;
import org.asmus.model.GamepadStateStream;
import org.asmus.model.PolarCoords;
import org.asmus.model.QualifiedEType;
import org.asmus.service.JoyWorker;
import org.asmus.tool.AxisButtonMapper;
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
                .map(AxisButtonMapper.mapVertical);

        Flux<QualifiedEType> horizontal = stateStream.getAxisFlux()
                .filter(q -> q.getHORIZONTAL_BTN() != 0)
                .map(AxisButtonMapper.mapHorizontal);

        return Flux.merge(vertical, horizontal)
                .publish().autoConnect();
    }

    public static Flux<PolarCoords> getLeftStickStream() {
        return stateStream.getAxisFlux()
                .map(new EventMapper(Gamepad::getLEFT_STICK_X, Gamepad::getLEFT_STICK_Y)::translateAxis);
    }

    public static Flux<PolarCoords> getRightStickStream() {
        return stateStream.getAxisFlux()
                .map(new EventMapper(Gamepad::getRIGHT_STICK_X, Gamepad::getRIGHT_STICK_Y)::translateAxis);
    }
}
