package com.github.finley243.adventureengine.network.action;

public interface NetworkAction {

    public void execute();

    public float detectionChance();

    public int cost();

}
