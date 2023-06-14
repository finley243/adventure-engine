package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public class Network {

    private final String ID;
    private final String name;
    private final NetworkNode topNode;

    public Network(String ID, String name, NetworkNode topNode) {
        this.ID = ID;
        this.name = name;
        this.topNode = topNode;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public List<Action> networkActions(Actor subject, String componentName) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(topNode.actions(subject, componentName));
        return actions;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Network other)) {
            return false;
        } else {
            return this.getID().equals(other.getID());
        }
    }

}
