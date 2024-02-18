package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeControl extends NetworkNode {

    private final String objectID;

    public NetworkNodeControl(String ID, String name, int securityLevel, String objectID) {
        super(ID, name, securityLevel);
        this.objectID = objectID;
    }

    @Override
    protected List<Action> breachedActions(Actor subject, WorldObject object) {
        return new ArrayList<>(subject.game().data().getObject(objectID).networkActions(subject, this));
    }

    public WorldObject getObject(Game game) {
        return game.data().getObject(objectID);
    }

}
