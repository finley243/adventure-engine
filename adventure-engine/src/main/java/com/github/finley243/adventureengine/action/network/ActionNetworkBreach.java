package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkBreach extends NetworkAction {

    private final NetworkNode node;
    private final WorldObject object;

    public ActionNetworkBreach(Actor subject, ActionDependencies dependencies, NetworkNode node, WorldObject object) {
        super(subject, dependencies);
        this.node = node;
        this.object = object;
    }

    @Override
    public String getID() {
        return "network_breach";
    }

    @Override
    public Context getContext() {
        Context context = Context.builder().subject(subject).parentObject(object).parentAction(this).build();
        context.setLocalVariable("node", Expression.valueHolder(node));
        return context;
    }

    @Override
    public void choose(int repeatActionCount) {
        node.setBreached(true);
    }

    @Override
    public CanChooseResult canChoose() {
        CanChooseResult resultSuper = super.canChoose();
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (node.isBreached()) {
            return new CanChooseResult(false, "Node already breached");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataNetwork(node);
    }

    @Override
    public String getPrompt() {
        return "Breach Node";
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
