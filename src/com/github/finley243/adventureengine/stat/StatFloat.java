package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;

public class StatFloat extends Stat {

    private float mod;
    private float mult;

    public StatFloat(String name, MutableStatHolder target) {
        super(name, target);
    }

    public float value(float base, float min, float max, Context context) {
        float computedValue = (base * (mult + 1.0f)) + mod;
        return Math.min(Math.max(computedValue, min), max);
    }

    public void addMod(float value) {
        mod += value;
        getTarget().onStatChange(getName());
    }

    public void addMult(float value) {
        mult += value;
        getTarget().onStatChange(getName());
    }

}
