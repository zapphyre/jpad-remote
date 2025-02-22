package org.asmus.builder.closure.button;

import org.asmus.model.TimedValue;

import java.util.List;

@FunctionalInterface
public interface OsDevice {

    void processButtonEvents(List<TimedValue> states);
}
