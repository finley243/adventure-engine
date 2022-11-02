package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ComponentLink {

    private final String object;
    private final AreaLink.CompassDirection direction;

    public ComponentLink(String object, AreaLink.CompassDirection direction) {
        this.object = object;
        this.direction = direction;
    }

    public String getObject() {
        return object;
    }

    public AreaLink.CompassDirection getDirection() {
        return direction;
    }

}
