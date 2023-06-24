package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NetworkNodeGroup extends NetworkNode {

    private final Set<NetworkNode> childNodes;

    public NetworkNodeGroup(String ID, String name, int securityLevel, Set<NetworkNode> childNodes) {
        super(ID, name, securityLevel);
        this.childNodes = childNodes;
    }

    @Override
    protected List<Action> breachedActions(Actor subject, WorldObject object, String[] menuPath) {
        List<Action> actions = new ArrayList<>();
        for (NetworkNode childNode : childNodes) {
            actions.addAll(childNode.actions(subject, object, menuPath));
        }
        return actions;
    }

}
