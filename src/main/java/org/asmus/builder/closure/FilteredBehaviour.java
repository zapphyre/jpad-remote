package org.asmus.builder.closure;

import org.asmus.model.BehavioralFilter;
import org.asmus.model.ButtonClick;

import java.util.function.Function;

@FunctionalInterface
public interface FilteredBehaviour {

    OsDevice act(Function<ButtonClick, BehavioralFilter> filter);
}
