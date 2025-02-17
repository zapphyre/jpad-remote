package org.asmus.builder.closure;

import org.asmus.service.JoyWorker;

@FunctionalInterface
public interface OsDevice {

    Actuation device(JoyWorker worker);
}
