package com.github.finley243.adventureengine.effect.moddable;

import com.github.finley243.adventureengine.MathUtils;

public class ModdableStatInt {

    private final Moddable target;
    private int mod;
    private float mult;

    public ModdableStatInt(Moddable target) {
        this.target = target;
    }

    public int value(int base, int min, int max) {
        int computedValue = Math.round(base * (mult + 1.0f)) + mod;
        return MathUtils.bound(computedValue, min, max);
    }

    public void addMod(int value) {
        mod += value;
        target.onStatChange();
    }

    public void addMult(float value) {
        mult += value;
        target.onStatChange();
    }

}
