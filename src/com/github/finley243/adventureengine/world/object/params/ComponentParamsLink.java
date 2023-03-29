package com.github.finley243.adventureengine.world.object.params;

import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.params.ComponentParams;

public class ComponentParamsLink implements ComponentParams {

    private final String object;
    private final AreaLink.CompassDirection direction;

    public ComponentParamsLink(String object, AreaLink.CompassDirection direction) {
        this.object = object;
        this.direction = direction;
    }

    @Override
    public Object getParameter(String key) {
        return switch (key) {
            case "object" -> object;
            case "direction" -> direction;
            default -> null;
        };
    }

}
