package com.github.finley243.adventureengine.actor;

public class ActorStat {

    private final int min;
    private final int max;
    private int base;
    private int mod;
    private float mult;

    public ActorStat(int min, int max){
        this.min = min;
        this.max = max;
    }

    public ActorStat(int defaultBase, int min, int max) {
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
    }

    public void addBase(int value) {
        base = Math.min(Math.max(base + value, min), max);
    }

    public void addMod(int value) {
        mod += value;
    }

    public void addMult(float value) {
        mult += value;
    }

}
