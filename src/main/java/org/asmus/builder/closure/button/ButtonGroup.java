package org.asmus.builder.closure.button;

import org.asmus.model.EButtonAxisMapping;

import java.util.List;

@FunctionalInterface
public interface ButtonGroup {

    OsDevice buttons(List<EButtonAxisMapping> buttons);
}
