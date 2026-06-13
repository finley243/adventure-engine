package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateNetwork;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentNetwork extends ObjectComponent {

    public ObjectComponentNetwork(Game game, WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private ObjectComponentTemplateNetwork getTemplateNetwork() {
        return (ObjectComponentTemplateNetwork) getTemplate();
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject) {
        NetworkNode networkNode = game.data().getNetworkNode(getObject().getLocalVariable("networkID").getValueString());
        return new ArrayList<>(networkNode.actions(game, subject, getObject()));
    }

    @Override
    protected String getStatName() {
        return "network";
    }

}
