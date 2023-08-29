package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.object.WorldObject;

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

    public List<Action> networkActions(Actor subject, WorldObject object) {
        return new ArrayList<>(topNode.actions(subject, object));
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
