package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class ControlNetworkNode extends NetworkNode {

    private String objectID;
    private WorldObject object;

    public ControlNetworkNode(String ID, String name, String objectID) {
        super(ID, name);
        this.objectID = objectID;
    }

    @Override
    public void resolveReferences(Registry<WorldObject> objectRegistry) {
        if (this.object != null) throw new IllegalStateException("ControlNetworkNode object has already been resolved");
        WorldObject object = objectRegistry.getFromID(objectID);
        if (object == null) throw new GameDataException("ControlNetworkNode has invalid object reference: " + objectID);
        this.object = object;
        this.objectID = null;
        super.resolveReferences(objectRegistry);
    }

    @Override
    protected List<Action> breachedActions(Actor subject, ActionDependencies dependencies, WorldObject object) {
        return new ArrayList<>(getObject().networkActions(subject, dependencies, this));
    }

    WorldObject getObject() {
        if (object == null) throw new IllegalStateException("ControlNetworkNode object has not been resolved");
        return object;
    }

}
