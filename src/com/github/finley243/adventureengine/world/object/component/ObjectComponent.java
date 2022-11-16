package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;
import java.util.Set;

public abstract class ObjectComponent implements StatHolder {

    private boolean isEnabled;
    private final String ID;
    private final WorldObject object;

    public ObjectComponent(String ID, WorldObject object, boolean startEnabled) {
        if (ID == null) throw new IllegalArgumentException("ObjectComponent ID cannot be null");
        if (ID.isEmpty()) throw new IllegalArgumentException("ObjectComponent ID cannot be empty");
        this.ID = ID;
        this.object = object;
        this.isEnabled = startEnabled;
    }

    public abstract List<Action> getActions(Actor subject);

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

    public void onNewGameInit() {}

    @Override
    public StatInt getStatInt(String name) {
        return null;
    }

    @Override
    public StatFloat getStatFloat(String name) {
        return null;
    }

    @Override
    public StatBoolean getStatBoolean(String name) {
        return null;
    }

    @Override
    public StatString getStatString(String name) {
        return null;
    }

    @Override
    public StatStringSet getStatStringSet(String name) {
        return null;
    }

    @Override
    public int getStatValueInt(String name) {
        return 0;
    }

    @Override
    public float getStatValueFloat(String name) {
        return 0;
    }

    @Override
    public boolean getStatValueBoolean(String name) {
        return false;
    }

    @Override
    public String getStatValueString(String name) {
        return null;
    }

    @Override
    public Set<String> getStatValueStringSet(String name) {
        return null;
    }

    @Override
    public void onStatChange() {

    }

    @Override
    public void setStateBoolean(String name, boolean value) {
        switch (name) {
            case "enabled":
                setEnabled(value);
                break;
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
    public void modifyStateInteger(String name, int amount) {

    }

    @Override
    public void modifyStateFloat(String name, float amount) {

    }

    @Override
    public void triggerEffect(String name) {

    }

}
