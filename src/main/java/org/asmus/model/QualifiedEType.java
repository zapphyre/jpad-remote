package org.asmus.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class QualifiedEType {
    EType type;
    EPressType pressType;
}
