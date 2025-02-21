package org.asmus.builder.closure;

import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.model.TimedValue;

import java.util.List;

@FunctionalInterface
public interface Actuation {

    ButtonGroup actuation(ActuationBehaviour behaviour);
}
