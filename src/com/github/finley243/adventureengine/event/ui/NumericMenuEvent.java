package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.menu.NumericMenuField;

import java.util.List;

public class NumericMenuEvent {

    private final List<NumericMenuField> numericFields;
    private final int points;

    public NumericMenuEvent(List<NumericMenuField> numericFields, int points) {
        this.numericFields = numericFields;
        this.points = points;
    }

    public List<NumericMenuField> getNumericFields() {
        return numericFields;
    }

    public int getPoints() {
        return points;
    }

}
