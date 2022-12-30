package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.network.NetworkNodeData;
import com.github.finley243.adventureengine.scene.SceneManager;

public class ActionNetworkReadData extends NetworkAction {

    private final NetworkNodeData node;

    public ActionNetworkReadData(NetworkNodeData node) {
        super();
        this.node = node;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        SceneManager.trigger(subject.game(), subject, subject, subject.game().data().getScene(node.getSceneID()));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Read Data", canChoose(subject), new String[] {"network", node.getName()}, new String[]{"read data from " + node.getName()});
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
