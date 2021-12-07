package com.github.finley243.adventureengine.world.object;

public class ObjectObstruction extends WorldObject {

    public ObjectObstruction(String name, String description) {
        super(name, description);
        setDimensions(2, 1);
    }

    @Override
    public boolean isPartialObstruction() {
        return true;
    }

}
