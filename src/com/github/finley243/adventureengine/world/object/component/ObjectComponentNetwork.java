package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateNetwork;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentNetwork extends ObjectComponent {

    public ObjectComponentNetwork(String ID, WorldObject object) {
        super(ID, object);
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateNetwork();
    }

    public ObjectComponentTemplateNetwork getTemplateNetwork() {
        return (ObjectComponentTemplateNetwork) getObject().getTemplate().getComponents().get(getID());
    }

    @Override
    public List<Action> getActions(Actor subject) {
        Network network = subject.game().data().getNetwork(getTemplateNetwork().getNetworkID());
        return new ArrayList<>(network.networkActions(subject, getTemplate().getName()));
    }

}
