package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.condition.Condition;

public class StatInt extends Stat {

    private int mod;
    private float mult;

    public StatInt(String name, MutableStatHolder target) {
        super(name, target);
    }

    public int value(int base, int min, int max, Context context) {
        int computedValue = Math.round(base * (mult + 1.0f)) + mod;
        return MathUtils.bound(computedValue, min, max);
    }

    public void addMod(int value) {
        mod += value;
        getTarget().onStatChange(getName());
    }

    public void addMult(float value) {
        mult += value;
        getTarget().onStatChange(getName());
    }

    public record StatIntMod(Condition condition, int add, int mult) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
