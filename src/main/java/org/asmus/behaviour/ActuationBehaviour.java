package org.asmus.behaviour;

import lombok.Builder;
import lombok.Value;
import org.asmus.introspect.Introspector;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.GamepadEvent;
import org.asmus.qualifier.Qualifier;
import org.asmus.qualifier.impl.TimedQualifier;
import reactor.core.publisher.Sinks;

@Value
@Builder
public class ActuationBehaviour {
    @Builder.Default
    Sinks.Many<GamepadEvent> out = Sinks.many().multicast().directBestEffort();

    @Builder.Default
    Introspector introspector = new ReleaseIntrospector();

    @Builder.Default
    Qualifier getQualifier = new TimedQualifier();
}
