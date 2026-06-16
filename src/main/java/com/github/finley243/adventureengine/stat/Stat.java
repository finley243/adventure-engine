package com.github.finley243.adventureengine.stat;

public abstract class Stat {

    private final String name;
    private final StatHolder target;

    public Stat(String name, StatHolder target) {
        this.name = name;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public StatHolder getTarget() {
        return target;
    }

}
