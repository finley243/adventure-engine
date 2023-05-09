package com.github.finley243.adventureengine.stat;

public class Stat {

    private final String name;
    private final MutableStatHolder target;

    public Stat(String name, MutableStatHolder target) {
        this.name = name;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public MutableStatHolder getTarget() {
        return target;
    }

}
