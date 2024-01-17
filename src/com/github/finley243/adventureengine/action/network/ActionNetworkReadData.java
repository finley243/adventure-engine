package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.network.NetworkNodeData;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkReadData extends NetworkAction {

    private final NetworkNodeData node;
    private final WorldObject object;

    public ActionNetworkReadData(NetworkNodeData node, WorldObject object) {
        this.node = node;
        this.object = object;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.game().menuManager().sceneMenu(subject.game(), subject.game().data().getScene(node.getSceneID()), null, new Context(subject.game(), subject, subject, object));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataNetwork(node);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Read Data";
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
