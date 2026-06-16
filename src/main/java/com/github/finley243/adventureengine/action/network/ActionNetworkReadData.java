package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.network.DataNetworkNode;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionNetworkReadData extends NetworkAction {

    private final DataNetworkNode node;
    private final WorldObject object;
    private final MenuManager menuManager;

    public ActionNetworkReadData(Actor subject, ActionDependencies dependencies, DataNetworkNode node, WorldObject object, MenuManager menuManager) {
        super(subject, dependencies);
        this.node = node;
        this.object = object;
        this.menuManager = menuManager;
    }

    @Override
    public String getID() {
        return "network_read";
    }

    @Override
    public Context getContext() {
        Context context = Context.builder().subject(subject).parentObject(object).parentAction(this).build();
        context.setLocalVariable("node", Expression.valueHolder(node));
        return context;
    }

    @Override
    public void choose(int repeatActionCount) {
        menuManager.sceneMenu(node.getScene(), getContext(), false);
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataNetwork(node);
    }

    @Override
    public String getPrompt() {
        return "Read Data";
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
