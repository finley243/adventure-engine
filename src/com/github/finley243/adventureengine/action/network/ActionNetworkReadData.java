package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.network.NetworkNodeData;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkReadData extends NetworkAction {

    private final NetworkNodeData node;
    private final WorldObject object;
    private final String[] menuPath;

    public ActionNetworkReadData(NetworkNodeData node, WorldObject object, String[] menuPath) {
        this.node = node;
        this.object = object;
        this.menuPath = menuPath;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.game().eventQueue().addToFront(new SceneEvent(subject.game().data().getScene(node.getSceneID()), null, new Context(subject.game(), subject, subject, object)));
        subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Read", canChoose(subject).canChoose(), menuPath, new String[]{"read data from " + node.getName()});
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
