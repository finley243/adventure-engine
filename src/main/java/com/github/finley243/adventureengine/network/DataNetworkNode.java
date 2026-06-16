package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.network.ActionNetworkReadData;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class DataNetworkNode extends NetworkNode {

    private final Scene scene;

    public DataNetworkNode(String ID, String name, Scene scene) {
        super(ID, name);
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    protected List<Action> breachedActions(Actor subject, ActionDependencies dependencies, WorldObject object) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionNetworkReadData(dependencies, this, object));
        return actions;
    }

}
