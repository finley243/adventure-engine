package com.github.finley243.adventureengine.event.ui;

import java.util.Map;

public class NumericMenuConfirmEvent {

    private final Map<String, Integer> changedValues;

    public NumericMenuConfirmEvent(Map<String, Integer> changedValues) {
        this.changedValues = changedValues;
    }

    public Map<String, Integer> getChangedValues() {
        return changedValues;
    }

}
