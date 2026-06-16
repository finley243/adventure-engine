package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkBreach extends NetworkAction {

    private final NetworkNode node;
    private final WorldObject object;

    public ActionNetworkBreach(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher, NetworkNode node, WorldObject object) {
        super(scriptRuntime, sensoryEventDispatcher);
        this.node = node;
        this.object = object;
    }

    @Override
    public String getID() {
        return "network_breach";
    }

    @Override
    public Context getContext(Actor subject) {
        Context context = Context.builder().subject(subject).parentObject(object).parentAction(this).build();
        context.setLocalVariable("node", Expression.valueHolder(node));
        return context;
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
