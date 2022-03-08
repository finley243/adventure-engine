package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public class SubNetwork {

    private final String name;
    private final List<String> connected;
    private final int securityLevel;
    private final List<SubNetwork> subNets;

    private boolean isBreached;

    public SubNetwork(String name, int securityLevel, List<String> connected, List<SubNetwork> subNets) {
        this.name = name;
        this.connected = connected;
        this.securityLevel = securityLevel;
        this.subNets = subNets;
    }

    public String getName() {
        return name;
    }

    public boolean isBreached() {
        return isBreached;
    }

    public List<Action> networkActions(Actor subject) {
        List<Action> actions = new ArrayList<>();

        for(SubNetwork subNet : subNets) {
            actions.addAll(subNet.networkActions(subject));
        }
        return actions;
    }

}
