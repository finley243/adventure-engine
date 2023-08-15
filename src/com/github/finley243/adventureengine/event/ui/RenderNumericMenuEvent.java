package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.menu.NumericMenuField;

import java.util.List;

public class RenderNumericMenuEvent {

    private final List<NumericMenuField> numericFields;
    private final int points;

    public RenderNumericMenuEvent(List<NumericMenuField> numericFields, int points) {
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
