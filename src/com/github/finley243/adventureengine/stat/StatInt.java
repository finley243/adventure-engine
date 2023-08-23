package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class StatInt extends Stat {

    private final List<StatIntMod> mods;
    private final int min;
    private final int max;

    public StatInt(String name, MutableStatHolder target, int min, int max) {
        super(name, target);
        this.mods = new ArrayList<>();
        this.min = min;
        this.max = max;
    }

    public int value(int base, Context context) {
        int add = 0;
        float mult = 0.0f;
        for (StatIntMod mod : mods) {
            if (mod.shouldApply(context)) {
                add += mod.add;
                mult += mod.mult;
            }
        }
        int computedValue = Math.round(base * (mult + 1.0f)) + add;
        return MathUtils.bound(computedValue, min, max);
    }

    public void addMod(StatIntMod mod) {
        mods.add(mod);
        getTarget().onStatChange(getName());
    }

    public void removeMod(StatIntMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(getName());
    }

    public record StatIntMod(Condition condition, int add, float mult) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
