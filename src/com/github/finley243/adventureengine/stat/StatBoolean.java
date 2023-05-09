package com.github.finley243.adventureengine.stat;

public class StatBoolean extends Stat {

    // If there are both true and false modifiers, the priority value will be used
    private final boolean priorityValue;
    private int countTrue;
    private int countFalse;

    public StatBoolean(String name, MutableStatHolder target, boolean priorityValue) {
        super(name, target);
        this.priorityValue = priorityValue;
    }

    public boolean value(boolean base) {
        if (countTrue == 0 && countFalse == 0) {
            return base;
        } else if (countTrue > 0 && countFalse > 0) {
            return priorityValue;
        } else {
            return countTrue > 0;
        }
    }

    public void addMod(boolean value) {
        if (value) {
            this.countTrue += 1;
        } else {
            this.countFalse += 1;
        }
        getTarget().onStatChange(getName());
    }

    public void removeMod(boolean value) {
        if (value) {
            this.countTrue -= 1;
        } else {
            this.countFalse -= 1;
        }
        getTarget().onStatChange(getName());
    }

}
