package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.network.ActionNetworkReadData;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeData extends NetworkNode {

    private final String sceneID;

    public NetworkNodeData(String ID, String name, int securityLevel, String sceneID) {
        super(ID, name, securityLevel);
        this.sceneID = sceneID;
    }

    public String getSceneID() {
        return sceneID;
    }

    @Override
    protected List<Action> breachedActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionNetworkReadData(this));
        return actions;
    }

}
