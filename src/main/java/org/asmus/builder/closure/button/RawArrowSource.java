package org.asmus.builder.closure.button;

import java.util.Map;

@FunctionalInterface
public interface RawArrowSource {

    void processArrowEvents(Map<String, Integer> axisStates);
}
