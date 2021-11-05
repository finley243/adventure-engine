package com.github.finley243.adventureengine.network.action;

import com.github.finley243.adventureengine.world.Networked;

public class NetworkAction {

    private final String action;
    private final Networked object;
    private final float detectionChance;
    private final int cost;

    public NetworkAction(String action, Networked object, float detectionChance, int cost) {
        this.action = action;
        this.object = object;
        this.detectionChance = detectionChance;
        this.cost = cost;
    }

    public void execute() {

    }

    public float detectionChance() {
        return detectionChance;
    }

    public int cost() {
        return cost;
    }

}
