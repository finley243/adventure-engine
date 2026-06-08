package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
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
    public String getID() {
        return "network_read";
    }

    @Override
    public Context getContext(Game game, Actor subject) {
        Context context = Context.builder(game).subject(subject).parentObject(object).parentAction(this).build();
        context.setLocalVariable("node", Expression.constant(node));
        return context;
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        game.menuManager().sceneMenu(game, game.data().getScene(node.getSceneID()), getContext(game, subject), false);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataNetwork(node);
    }

    @Override
    public String getPrompt(Game game, Actor subject) {
        return "Read Data";
    }

    @Override
    public float networkDetectionChance() {
        return 0;
    }

}
