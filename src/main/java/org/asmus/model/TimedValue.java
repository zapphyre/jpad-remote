package org.asmus.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class TimedValue {
    @EqualsAndHashCode.Exclude
    long time = System.currentTimeMillis();

    String name;
    boolean value;

    public TimedValue(String name) {
        this.name = name;
        this.value = false;
    }

    public TimedValue(InputValue<Boolean> iv) {
        this.name = iv.name();
        this.value = iv.value();
    }

}
