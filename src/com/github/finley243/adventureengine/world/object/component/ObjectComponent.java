package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.*;
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
    public int getValueInt(String name) {
        return 0;
    }

    @Override
    public float getValueFloat(String name) {
        return 0;
    }

    @Override
    public boolean getValueBoolean(String name) {
        return false;
    }

    @Override
    public String getValueString(String name) {
        switch (name) {
            case "id":
                return getID();
            case "templateID":
                return getTemplate().getID();
        }
        return null;
    }

    @Override
    public Set<String> getValueStringSet(String name) {
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
    public void modStateInteger(String name, int amount) {

    }

    @Override
    public void modStateFloat(String name, float amount) {

    }

    @Override
    public void triggerEffect(String name) {

    }

}
