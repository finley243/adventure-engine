package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.NetworkObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public class NetworkObjectComponent extends ObjectComponent {

    private NetworkNode networkNode;

    NetworkObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private NetworkObjectComponentTemplate getTemplateNetwork() {
        return (NetworkObjectComponentTemplate) getTemplate();
    }

    public void resolveNetwork(NetworkNode networkNode) {
        if (this.networkNode != null) throw new IllegalStateException("Network has already been resolved");
        this.networkNode = networkNode;
    }

    private NetworkNode getNetworkNode() {
        if (this.networkNode == null) throw new IllegalStateException("Network has not been resolved");
        return networkNode;
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject, ActionDependencies dependencies) {
        NetworkNode networkNode = getNetworkNode();
        return new ArrayList<>(networkNode.actions(subject, dependencies, getObject()));
    }

    @Override
    protected String getStatName() {
        return "network";
    }

}
