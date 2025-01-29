package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Set;

@With
@Value
@Builder
public class QualifiedEType {
    EType type;
    EMultiplicity multiplicity;
    boolean longPress;
    List<EType> modifiers;
}
