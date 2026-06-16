package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.NetworkObjectComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public class NetworkObjectComponent extends ObjectComponent {

    NetworkObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private NetworkObjectComponentTemplate getTemplateNetwork() {
        return (NetworkObjectComponentTemplate) getTemplate();
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject, ScriptRuntime scriptRuntime) {
        NetworkNode networkNode = game.data().getNetworkNode(getObject().getLocalVariable("networkID").getValueString());
        return new ArrayList<>(networkNode.actions(game, subject, getObject()));
    }

    @Override
    protected String getStatName() {
        return "network";
    }

}
