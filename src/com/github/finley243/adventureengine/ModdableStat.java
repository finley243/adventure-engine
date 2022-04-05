package com.github.finley243.adventureengine;

public class ModdableStat {

    private final Moddable target;
    private int mod;
    private float mult;

    public ModdableStat(Moddable target) {
        this.target = target;
    }

    public int value(int base, int min, int max) {
        int computedValue = Math.round(base * (mult + 1.0f)) + mod;
        return Math.min(Math.max(computedValue, min), max);
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
