package org.asmus.builder.closure;

import java.util.Map;

@FunctionalInterface
public interface RawArrowSource {

    void processArrowEvents(Map<String, Integer> axisStates);
}
