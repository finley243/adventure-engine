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
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (node.isBreached()) {
            return new CanChooseResult(false, "Node already breached");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Breach Node", canChoose(subject).canChoose(), menuPath, new String[]{"breach " + node.getName()});
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
