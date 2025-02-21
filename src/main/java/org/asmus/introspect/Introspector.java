package org.asmus.introspect;

import org.asmus.model.ButtonClick;

import java.util.Set;

public interface Introspector {

    ButtonClick translate(ButtonClick buttonClick);

    default Set<String> getModifiersResetEvents() {
        return Set.of();
    }
}
