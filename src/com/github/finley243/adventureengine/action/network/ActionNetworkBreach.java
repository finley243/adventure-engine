package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkBreach extends NetworkAction {

    private final NetworkNode node;
    private final WorldObject object;

    public ActionNetworkBreach(NetworkNode node, WorldObject object) {
        this.node = node;
        this.object = object;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        node.setBreached(true);
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
        subject.game().eventQueue().executeNext();
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
    public ActionCategory getCategory(Actor subject) {
        return ActionCategory.NETWORK;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataNetwork(node);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Breach Node";
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
