package org.asmus.builder.closure;

import org.asmus.model.EButtonAxisMapping;

import java.util.List;

@FunctionalInterface
public interface ButtonGroup {

    OsDevice buttons(List<EButtonAxisMapping> buttons);
}
