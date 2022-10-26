package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.scene.Scene;

public class ObjectTemplate {

    private final String ID;
    private final String name;
    private final Scene description;

    public ObjectTemplate(String ID, String name, Scene description) {
        this.ID = ID;
        this.name = name;
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Scene getDescription() {
        return description;
    }

}
