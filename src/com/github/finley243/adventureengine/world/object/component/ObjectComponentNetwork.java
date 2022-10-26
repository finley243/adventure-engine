package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentNetwork extends ObjectComponent {

    private final String networkID;

    public ObjectComponentNetwork(String ID, WorldObject object, boolean startEnabled, String networkID) {
        super(ID, object, startEnabled);
        this.networkID = networkID;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        Network network = subject.game().data().getNetwork(networkID);
        actions.addAll(network.networkActions(subject));
        return actions;
    }

}
