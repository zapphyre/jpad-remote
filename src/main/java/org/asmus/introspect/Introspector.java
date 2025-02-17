package org.asmus.introspect;

import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.List;
import java.util.function.Function;

public interface Introspector {

    Function<List<TimedValue>, ButtonClick> translate(List<String> forButtonNames);
}
