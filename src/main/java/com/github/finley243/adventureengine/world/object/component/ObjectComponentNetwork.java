package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateNetwork;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentNetwork extends ObjectComponent {

    public ObjectComponentNetwork(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private ObjectComponentTemplateNetwork getTemplateNetwork() {
        return (ObjectComponentTemplateNetwork) getTemplate();
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject) {
        NetworkNode networkNode = subject.game().data().getNetworkNode(getObject().getLocalVariable("networkID").getValueString());
        return new ArrayList<>(networkNode.actions(subject, getObject()));
    }

    @Override
    protected String getStatName() {
        return "network";
    }

}
