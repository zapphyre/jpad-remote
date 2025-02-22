package org.asmus.builder.closure.button;

import org.asmus.behaviour.ActuationBehaviour;

@FunctionalInterface
public interface Actuation {

    ButtonGroup actuation(ActuationBehaviour behaviour);
}
