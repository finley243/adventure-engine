package com.github.finley243.adventureengine.stat;

public class StatFloat {

    private final StatHolder target;
    private float mod;
    private float mult;

    public StatFloat(StatHolder target) {
        this.target = target;
    }

    public float value(float base, float min, float max) {
        float computedValue = (base * (mult + 1.0f)) + mod;
        return Math.min(Math.max(computedValue, min), max);
    }

    public void addMod(float value) {
        mod += value;
        target.onStatChange();
    }

    public void addMult(float value) {
        mult += value;
        target.onStatChange();
    }

}