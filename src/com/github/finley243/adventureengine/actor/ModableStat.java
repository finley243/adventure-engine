package com.github.finley243.adventureengine.actor;

public class ModableStat {

    private final Actor actor;
    private final int min;
    private final int max;
    private int base;
    private int mod;
    private float mult;

    public ModableStat(Actor actor, int defaultBase, int min, int max) {
        this.actor = actor;
        this.min = min;
        this.max = max;
        this.base = defaultBase;
    }

    public int value() {
        int computedValue = Math.round(base * (mult + 1.0f)) + mod;
        return Math.min(Math.max(computedValue, min), max);
    }

    public int valueBase() {
        return base;
    }

    public void setBase(int value) {
        base = Math.min(Math.max(value, min), max);
        actor.onStatChange();
    }

    public void addBase(int value) {
        base = Math.min(Math.max(base + value, min), max);
        actor.onStatChange();
    }

    public void addMod(int value) {
        mod += value;
        actor.onStatChange();
    }

    public void addMult(float value) {
        mult += value;
        actor.onStatChange();
    }

}
