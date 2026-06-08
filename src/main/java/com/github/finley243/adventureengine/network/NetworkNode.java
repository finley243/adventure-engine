package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.network.ActionNetworkBreach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class NetworkNode extends GameInstanced implements StatHolder {

    private final String templateID;
    private NetworkNodeTemplate template;
    private final String name;

    private boolean isBreached;

    public NetworkNode(Game game, String ID, String templateID, String name) {
        super(ID);
        this.templateID = templateID;
        this.name = name;
    }

    public void init(Game game, Map<String, WorldObject> objects) {
        this.template = game.data().getNetworkNodeTemplate(templateID);
    }

    private NetworkNodeTemplate getTemplate() {
        if (template == null) throw new IllegalStateException("NetworkNode has not been initialized");
        return template;
    }

    public String getName() {
        return name;
    }

    public boolean isBreached() {
        return isBreached;
    }

    public void setBreached(boolean state) {
        this.isBreached = state;
    }

    public List<Action> actions(Game game, Actor subject, WorldObject object) {
        List<Action> actions = new ArrayList<>();
        if (isBreached()) {
            actions.addAll(breachedActions(game, subject, object));
        } else {
            actions.add(new ActionNetworkBreach(this, object));
        }
        return actions;
    }

    protected abstract List<Action> breachedActions(Game game, Actor subject, WorldObject object);

    @Override
    public Expression getStatValue(String name, Context context, Game game) {
        return switch (name) {
            case "id" -> Expression.constant(getID());
            case "name" -> Expression.constant(name);
            case "isBreached" -> Expression.constant(isBreached);
            default -> null;
        };
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context, Game game) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NetworkNode && ((NetworkNode) o).getID().equals(this.getID());
    }

    @Override
    public int hashCode() {
        return this.getID().hashCode();
    }

}
