package com.github.finley243.adventureengine.world.object.template;

public abstract class ObjectComponentTemplate {

    private final boolean startEnabled;
    // If true, actions are not added to object actions
    private final boolean actionsRestricted;

    public ObjectComponentTemplate(boolean startEnabled, boolean actionsRestricted) {
        this.startEnabled = startEnabled;
        this.actionsRestricted = actionsRestricted;
    }

    public boolean startEnabled() {
        return startEnabled;
    }

    public boolean actionsRestricted() {
        return actionsRestricted;
    }

}
