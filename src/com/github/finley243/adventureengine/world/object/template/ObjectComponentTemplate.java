package com.github.finley243.adventureengine.world.object.template;

public abstract class ObjectComponentTemplate {

    private final String ID;
    private final boolean startEnabled;

    public ObjectComponentTemplate(String ID, boolean startEnabled) {
        this.ID = ID;
        this.startEnabled = startEnabled;
    }

    public String getID() {
        return ID;
    }

    public boolean startEnabled() {
        return startEnabled;
    }

}
