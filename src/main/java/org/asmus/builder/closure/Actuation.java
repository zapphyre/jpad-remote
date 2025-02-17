package org.asmus.builder.closure;

import org.asmus.behaviour.ActuationBehaviour;

@FunctionalInterface
public interface Actuation {

    ButtonGroup actuation(ActuationBehaviour behaviour);
}
