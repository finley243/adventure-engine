package com.github.finley243.adventureengine.stat;

public class StatBoolean {

    private final StatHolder target;
    // If there are both true and false modifiers, the priority value will be used
    private final boolean priorityValue;
    private int countTrue;
    private int countFalse;

    public StatBoolean(StatHolder target, boolean priorityValue) {
        this.target = target;
        this.priorityValue = priorityValue;
    }

    public boolean value(boolean base) {
        if (countTrue == 0 && countFalse == 0) {
            return base;
        } else if (countTrue > 0 && countFalse > 0) {
            return priorityValue;
        } else if (countTrue > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addMod(boolean value) {
        if (value) {
            this.countTrue += 1;
        } else {
            this.countFalse += 1;
        }
        target.onStatChange();
    }

    public void removeMod(boolean value) {
        if (value) {
            this.countTrue -= 1;
        } else {
            this.countFalse -= 1;
        }
        target.onStatChange();
    }

}
