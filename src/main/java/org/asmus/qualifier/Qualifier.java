package org.asmus.qualifier;

import org.asmus.model.ButtonClick;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

public interface Qualifier {

    BiConsumer<ButtonClick, SynchronousSink<GamepadEvent>> qualify();
}
