package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeControl extends NetworkNode {

    private final String objectID;

    public NetworkNodeControl(String ID, String name, int securityLevel, String objectID) {
        super(ID, name, securityLevel);
        this.objectID = objectID;
    }

    @Override
    protected List<Action> breachedActions(Actor subject) {
        List<Action> actions = new ArrayList<>();

        return actions;
    }

}
