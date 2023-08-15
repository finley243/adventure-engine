package com.github.finley243.adventureengine.event.ui;

import java.util.Map;

public class NumericMenuInputEvent {

    private final Map<String, Integer> values;

    public NumericMenuInputEvent(Map<String, Integer> values) {
        this.values = values;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

}
