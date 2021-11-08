package com.github.finley243.adventureengine.network.action;

import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.world.Networked;

public class NetworkAction {

    private final String action;
    private final Networked object;
    private final float detectionChance;
    private final int cost;
    private final String prompt;
    private final MenuData menuData;

    public NetworkAction(String action, Networked object, float detectionChance, int cost, String prompt, MenuData menuData) {
        this.action = action;
        this.object = object;
        this.detectionChance = detectionChance;
        this.cost = cost;
        this.prompt = prompt;
        this.menuData = menuData;
    }

    public void execute() {

    }

    public float detectionChance() {
        return detectionChance;
    }

    public int cost() {
        return cost;
    }

    public String getPrompt() {
        return prompt;
    }

    public MenuData getMenuData() {
        return menuData;
    }

}
