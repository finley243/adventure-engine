package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

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

    public boolean value(boolean base, Game game, Context context) {
        int countTrue = 0;
        int countFalse = 0;
        for (StatBooleanMod mod : mods) {
            if (mod.shouldApply(game, context)) {
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

    public void addMod(StatBooleanMod mod, Game game) {
        mods.add(mod);
        getTarget().onStatChange(getName(), game);
    }

    public void removeMod(StatBooleanMod mod, Game game) {
        mods.remove(mod);
        getTarget().onStatChange(getName(), game);
    }

    public boolean getPriorityValue() {
        return priorityValue;
    }

    public List<StatBooleanMod> getMods() {
        return mods;
    }

    public record StatBooleanMod(Condition condition, boolean value) {
        public boolean shouldApply(Game game, Context context) {
            return condition == null || condition.isMet(game, context);
        }
    }

}
