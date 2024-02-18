package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.network.ActionNetworkBreach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NetworkNode implements StatHolder {

    private final String ID;
    private final String name;
    private final int securityLevel;

    private boolean isBreached;

    public NetworkNode(String ID, String name, int securityLevel) {
        this.ID = ID;
        this.name = name;
        this.securityLevel = securityLevel;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public boolean isBreached() {
        return isBreached;
    }

    public void setBreached(boolean state) {
        this.isBreached = state;
    }

    public List<Action> actions(Actor subject, WorldObject object) {
        List<Action> actions = new ArrayList<>();
        /*String[] currentNodePath = Arrays.copyOf(menuPath, menuPath.length + 1);
        currentNodePath[currentNodePath.length - 1] = getName();*/
        if (isBreached()) {
            actions.addAll(breachedActions(subject, object));
        } else {
            actions.add(new ActionNetworkBreach(this, object));
        }
        return actions;
    }

    protected abstract List<Action> breachedActions(Actor subject, WorldObject object);

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "id" -> Expression.constant(ID);
            case "name" -> Expression.constant(name);
            case "securityLevel" -> Expression.constant(securityLevel);
            case "isBreached" -> Expression.constant(isBreached);
            default -> null;
        };
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
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
