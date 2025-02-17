package org.asmus.introspect;

import org.asmus.model.ButtonClick;
import org.asmus.model.TimedValue;

import java.util.List;

public interface Introspector {

    ButtonClick translate(List<TimedValue> states);
}
