package com.github.finley243.adventureengine.menu;

public class NumericMenuField {

    private final String ID;
    private final String name;
    private final int min;
    private final int max;
    private final int initial;

    public NumericMenuField(String ID, String name, int min, int max, int initial) {
        this.ID = ID;
        this.name = name;
        this.min = min;
        this.max = max;
        this.initial = initial;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getInitial() {
        return initial;
    }

}
