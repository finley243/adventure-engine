package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeData extends NetworkNode {

    public NetworkNodeData(String ID, String name, int securityLevel) {
        super(ID, name, securityLevel);
    }

    @Override
    protected List<Action> breachedActions(Actor subject) {
        List<Action> actions = new ArrayList<>();

        return actions;
    }

}
