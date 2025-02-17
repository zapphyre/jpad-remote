package org.asmus.qualifier.impl;

import org.asmus.model.ButtonClick;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public class ImmediateQualifier extends BaseQualifier {

    @Override
    public BiConsumer<ButtonClick, SynchronousSink<GamepadEvent>> qualify() {
        return (q, p) -> p.next(toGamepadEventWith(q));
    }
}
