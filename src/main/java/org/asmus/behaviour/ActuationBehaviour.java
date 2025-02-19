package org.asmus.behaviour;

import lombok.Builder;
import lombok.Value;
import org.asmus.introspect.Introspector;
import org.asmus.qualifier.Qualifier;

@Value
@Builder
public class ActuationBehaviour {

    Introspector introspector;
    Qualifier qualifier;
}
