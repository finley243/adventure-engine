package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class StatFloat extends Stat {

    private final List<StatFloatMod> mods;
    private final Float min;
    private final Float max;

    public StatFloat(String name, MutableStatHolder target, Float min, Float max) {
        super(name, target);
        this.mods = new ArrayList<>();
        this.min = min;
        this.max = max;
    }

    public float value(float base, Context context) {
        float add = 0.0f;
        float mult = 0.0f;
        for (StatFloatMod mod : mods) {
            if (mod.shouldApply(context)) {
                add += mod.add;
                mult += mod.mult;
            }
        }
        float computedValue = (base * (mult + 1.0f)) + add;
        if (min != null) {
            computedValue = Math.max(min, computedValue);
        }
        if (max != null) {
            computedValue = Math.min(max, computedValue);
        }
        return computedValue;
    }

    public void addMod(StatFloatMod mod) {
        mods.add(mod);
        getTarget().onStatChange(getName());
    }

    public void removeMod(StatFloatMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(getName());
    }

    public record StatFloatMod(Condition condition, float add, float mult) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
