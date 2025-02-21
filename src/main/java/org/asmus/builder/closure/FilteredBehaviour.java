package org.asmus.builder.closure;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.model.ButtonClick;

import java.util.function.Function;

@FunctionalInterface
public interface FilteredBehaviour {

    OsDevice act(Function<ButtonClick, ActuationBehaviour> filter);
}
