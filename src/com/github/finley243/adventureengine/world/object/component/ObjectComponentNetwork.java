package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.Network;
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
    public List<Action> getActions(Actor subject) {
        Context context = new Context(subject.game(), subject, subject, getObject());
        Network network = subject.game().data().getNetwork(getObject().getLocalVariable("networkID").getValueString());
        return new ArrayList<>(network.networkActions(subject, getObject()));
    }

    @Override
    protected String getStatName() {
        return "network";
    }

}
