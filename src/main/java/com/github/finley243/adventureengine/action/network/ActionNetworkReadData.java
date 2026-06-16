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

    public ActionNetworkReadData(ActionDependencies dependencies, DataNetworkNode node, WorldObject object, MenuManager menuManager) {
        super(dependencies);
        this.node = node;
        this.object = object;
        this.menuManager = menuManager;
    }

    @Override
    public String getID() {
        return "network_read";
    }

    @Override
    public Context getContext(Actor subject) {
        Context context = Context.builder().subject(subject).parentObject(object).parentAction(this).build();
        context.setLocalVariable("node", Expression.valueHolder(node));
        return context;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        menuManager.sceneMenu(node.getScene(), getContext(subject), false);
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
