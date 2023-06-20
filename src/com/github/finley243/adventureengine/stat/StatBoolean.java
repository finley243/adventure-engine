package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

public class StatBoolean extends Stat {

    // If there are both true and false modifiers, the priority value will be used
    private final boolean priorityValue;
    private int countTrue;
    private int countFalse;

    public StatBoolean(String name, MutableStatHolder target, boolean priorityValue) {
        super(name, target);
        this.priorityValue = priorityValue;
    }

    public boolean value(boolean base, Context context) {
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

    public record StatBooleanMod(Condition condition, boolean value) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
