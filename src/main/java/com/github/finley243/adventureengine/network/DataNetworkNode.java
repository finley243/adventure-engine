package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.network.ActionNetworkReadData;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class DataNetworkNode extends NetworkNode {

    private final String sceneID;

    public DataNetworkNode(String ID, String name, String sceneID) {
        super(ID, name);
        this.sceneID = sceneID;
    }

    public String getSceneID() {
        return sceneID;
    }

    @Override
    protected List<Action> breachedActions(Game game, Actor subject, WorldObject object) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionNetworkReadData(this, object));
        return actions;
    }

}
