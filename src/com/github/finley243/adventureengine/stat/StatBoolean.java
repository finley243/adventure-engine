package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

import java.util.ArrayList;
import java.util.List;

public class StatBoolean extends Stat {

    // If there are both true and false modifiers, the priority value will be used
    private final boolean priorityValue;
    private final List<StatBooleanMod> mods;

    public StatBoolean(String name, MutableStatHolder target, boolean priorityValue) {
        super(name, target);
        this.priorityValue = priorityValue;
        this.mods = new ArrayList<>();
    }

    public boolean value(boolean base, Context context) {
        int countTrue = 0;
        int countFalse = 0;
        for (StatBooleanMod mod : mods) {
            if (mod.shouldApply(context)) {
                if (mod.value) {
                    countTrue += 1;
                } else {
                    countFalse += 1;
                }
            }
        }
        if (countTrue == 0 && countFalse == 0) {
            return base;
        } else if (countTrue > 0 && countFalse > 0) {
            return priorityValue;
        } else {
            return countTrue > 0;
        }
    }

    public void addMod(StatBooleanMod mod) {
        mods.add(mod);
        getTarget().onStatChange(getName());
    }

    public void removeMod(StatBooleanMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(getName());
    }

    public boolean getPriorityValue() {
        return priorityValue;
    }

    public List<StatBooleanMod> getMods() {
        return mods;
    }

    public record StatBooleanMod(Script condition, boolean value) {
        public boolean shouldApply(Context context) {
            if (condition == null) return true;
            Expression conditionResult = condition.execute().value();
            if (conditionResult.getDataType(context) != Expression.DataType.BOOLEAN) throw new IllegalArgumentException("Condition provided non-boolean value");
            return conditionResult.getValueBoolean(context);
        }
    }

}
