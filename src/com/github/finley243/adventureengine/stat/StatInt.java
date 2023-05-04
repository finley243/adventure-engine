package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.MathUtils;

public class StatInt extends Stat {

    private int mod;
    private float mult;

    public StatInt(String name, EffectableStatHolder target) {
        super(name, target);
    }

    public int value(int base, int min, int max) {
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

}
