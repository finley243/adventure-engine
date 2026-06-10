package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkNodeControl extends NetworkNode {

    private final String objectID;
    private WorldObject object;

    public NetworkNodeControl(String ID, String name, String objectID) {
        super(ID, name);
        this.objectID = objectID;
    }

    @Override
    public void init(Game game, Map<String, WorldObject> objects) {
        super.init(game, objects);
        this.object = objects.get(objectID);
    }

    @Override
    protected List<Action> breachedActions(Game game, Actor subject, WorldObject object) {
        return new ArrayList<>(game.data().getObject(objectID).networkActions(game, subject, this));
    }

    public WorldObject getObject() {
        return object;
    }

}
