package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.network.NetworkNode;

public class ActionNetworkBreach extends NetworkAction {

    private final NetworkNode node;

    public ActionNetworkBreach(NetworkNode node) {
        super();
        this.node = node;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        node.setBreached(true);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Breach Node", canChoose(subject), new String[] {"network", node.getName()});
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
