package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;

import java.util.List;
import java.util.Set;

public abstract class ObjectComponent implements StatHolder {

    private boolean isEnabled;
    private final String ID;
    private final WorldObject object;

    public ObjectComponent(String ID, WorldObject object) {
        if (ID == null) throw new IllegalArgumentException("ObjectComponent ID cannot be null");
        if (ID.isEmpty()) throw new IllegalArgumentException("ObjectComponent ID cannot be empty");
        this.ID = ID;
        this.object = object;
    }

    public abstract ObjectComponentTemplate getTemplate();

    public abstract List<Action> getActions(Actor subject);

    public boolean actionsRestricted() {
        return getTemplate().actionsRestricted();
    }

    public String getID() {
        return ID;
    }

    public WorldObject getObject() {
        return object;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void onNewGameInit() {
        this.isEnabled = getTemplate().startEnabled();
    }

    public void onSetObjectEnabled(boolean enable) {}

    public void onSetObjectArea(Area area) {}

    @Override
    public int getValueInt(String name, Context context) {
        return 0;
    }

    @Override
    public float getValueFloat(String name, Context context) {
        return 0;
    }

    @Override
    public boolean getValueBoolean(String name, Context context) {
        return false;
    }

    @Override
    public String getValueString(String name, Context context) {
        if ("id".equals(name)) {
            return getID();
        }
        return null;
    }

    @Override
    public Set<String> getValueStringSet(String name, Context context) {
        return null;
    }

    @Override
    public void setStateBoolean(String name, boolean value) {
        if ("enabled".equals(name)) {
            setEnabled(value);
        }
    }

    @Override
    public void setStateInteger(String name, int value) {

    }

    @Override
    public void setStateFloat(String name, float value) {

    }

    @Override
    public void setStateString(String name, String value) {

    }

    @Override
    public void setStateStringSet(String name, Set<String> value) {

    }

    @Override
    public void modStateInteger(String name, int amount) {

    }

    @Override
    public void modStateFloat(String name, float amount) {

    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjectComponent component)) {
            return false;
        } else if (!(component.getClass().equals(this.getClass()))) {
            return false;
        } else {
            return component.getObject().equals(this.getObject()) && component.getID().equals(this.getID());
        }
    }

}
