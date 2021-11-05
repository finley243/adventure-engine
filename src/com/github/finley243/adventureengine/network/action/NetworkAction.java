package com.github.finley243.adventureengine.network.action;

public class NetworkAction {

    private final String action;
    private final float detectionChance;
    private final int cost;

    public NetworkAction(String action, float detectionChance, int cost) {
        this.action = action;
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
