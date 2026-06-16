package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

public class BooleanStat extends Stat {

    // If there are both true and false modifiers, the priority value will be used
    private final boolean priorityValue;
    private final List<StatBooleanMod> mods;

    public BooleanStat(String name, StatHolder target, ScriptRuntime scriptRuntime, boolean priorityValue) {
        super(name, target, scriptRuntime);
        this.priorityValue = priorityValue;
        this.mods = new ArrayList<>();
    }

    public boolean value(boolean base, Context context) {
        int countTrue = 0;
        int countFalse = 0;
        for (StatBooleanMod mod : mods) {
            if (shouldApplyMod(mod.condition(), context)) {
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

    public record StatBooleanMod(Condition condition, boolean value) {}

}
