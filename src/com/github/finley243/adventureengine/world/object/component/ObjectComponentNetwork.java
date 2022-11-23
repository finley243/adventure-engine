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

    private final ObjectComponentTemplateNetwork template;

    public ObjectComponentNetwork(String ID, WorldObject object, ObjectComponentTemplateNetwork template) {
        super(ID, object, template.startEnabled());
        this.template = template;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return template;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        Network network = subject.game().data().getNetwork(template.getNetworkID());
        actions.addAll(network.networkActions(subject));
        return actions;
    }

}
