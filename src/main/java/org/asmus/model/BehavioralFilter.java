package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import org.asmus.behaviour.ActuationBehaviour;

import java.util.List;

@Value
@Builder
public class BehavioralFilter {
    ActuationBehaviour behaviour;
    List<EButtonAxisMapping> buttons;
}
