package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkBreach extends NetworkAction {

    private final NetworkNode node;
    private final WorldObject object;
    private final String[] menuPath;

    public ActionNetworkBreach(NetworkNode node, WorldObject object, String[] menuPath) {
        this.node = node;
        this.object = object;
        this.menuPath = menuPath;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        node.setBreached(true);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && !node.isBreached();
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Breach Node", canChoose(subject), menuPath, new String[]{"breach " + node.getName()});
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
