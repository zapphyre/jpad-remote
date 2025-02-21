package org.asmus.behaviour;

import lombok.Builder;
import lombok.Value;
import org.asmus.introspect.Introspector;
import org.asmus.qualifier.QualifyBuilder;

@Value
@Builder
public class ActuationBehaviour {

    Introspector introspector;
    QualifyBuilder qualifier;
}
