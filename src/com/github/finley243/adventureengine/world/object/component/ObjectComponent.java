package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public abstract class ObjectComponent {

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

}
