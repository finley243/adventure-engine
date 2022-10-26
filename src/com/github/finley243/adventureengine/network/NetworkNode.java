package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.network.ActionNetworkBreach;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkNode {

    private final String ID;
    private final String name;
    private final int securityLevel;

    private boolean isBreached;

    public NetworkNode(String ID, String name, int securityLevel) {
        this.ID = ID;
        this.name = name;
        this.securityLevel = securityLevel;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public boolean isBreached() {
        return isBreached;
    }

    public void setBreached(boolean state) {
        this.isBreached = state;
    }

    public List<Action> actions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (isBreached()) {
            actions.addAll(breachedActions(subject));
        } else {
            actions.add(new ActionNetworkBreach(this));
        }
        return actions;
    }

    protected abstract List<Action> breachedActions(Actor subject);

    @Override
    public boolean equals(Object o) {
        return o instanceof NetworkNode && ((NetworkNode) o).getID().equals(this.getID());
    }

    @Override
    public int hashCode() {
        return this.getID().hashCode();
    }

}
