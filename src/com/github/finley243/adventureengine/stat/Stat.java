package com.github.finley243.adventureengine.stat;

public class Stat {

    private final String name;
    private final EffectableStatHolder target;

    public Stat(String name, EffectableStatHolder target) {
        this.name = name;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public EffectableStatHolder getTarget() {
        return target;
    }

}
