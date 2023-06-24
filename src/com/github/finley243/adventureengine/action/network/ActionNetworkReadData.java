package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.network.NetworkNodeData;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.textgen.LangUtils;
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
        SceneManager.trigger(new Context(subject.game(), subject, subject, object), subject.game().data().getScene(node.getSceneID()));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Read", canChoose(subject), menuPath, new String[]{"read data from " + node.getName()});
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
