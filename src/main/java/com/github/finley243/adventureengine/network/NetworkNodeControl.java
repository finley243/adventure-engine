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

    public NetworkNodeControl(Game game, String ID, String templateID, String name, String objectID) {
        super(game, ID, templateID, name);
        this.objectID = objectID;
    }

    @Override
    public void init(Map<String, WorldObject> objects) {
        this.object = objects.get(objectID);
    }

    @Override
    protected List<Action> breachedActions(Actor subject, WorldObject object) {
        return new ArrayList<>(subject.game().data().getObject(objectID).networkActions(subject, this));
    }

    public WorldObject getObject() {
        return object;
    }

}
