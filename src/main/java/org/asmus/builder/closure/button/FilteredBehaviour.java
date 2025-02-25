package org.asmus.builder.closure.button;

import org.asmus.model.ButtonClick;

import java.util.function.Consumer;

@FunctionalInterface
public interface FilteredBehaviour {

    OsDevice act(Consumer<ButtonClick> qualifyWanted);
}
