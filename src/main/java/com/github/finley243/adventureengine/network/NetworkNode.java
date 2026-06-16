package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.network.ActionNetworkBreach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class NetworkNode extends GameInstanced implements ScriptValueHolder {

    private final String name;

    private boolean isBreached;

    public NetworkNode(String ID, String name) {
        super(ID);
        this.name = name;
    }

    public void init(Game game, Map<String, WorldObject> objects) {

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
    public Expression getScriptValue(String name, Context context) {
        return switch (name) {
            case "id" -> Expression.string(getID());
            case "name" -> Expression.string(name);
            case "isBreached" -> Expression.bool(isBreached);
            default -> null;
        };
    }

    @Override
    public boolean setScriptValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public ScriptValueHolder getSubHolder(String name, String ID) {
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
