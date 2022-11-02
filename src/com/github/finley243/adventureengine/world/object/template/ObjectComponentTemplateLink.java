package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ObjectComponentTemplateLink extends ObjectComponentTemplate {

    private final boolean isMovable;
    private final boolean isVisible;
    private final AreaLink.CompassDirection direction;

    public ObjectComponentTemplateLink(Game game, String ID, boolean startEnabled, boolean isMovable, boolean isVisible, AreaLink.CompassDirection direction) {
        super(game, ID, startEnabled);
        this.isMovable = isMovable;
        this.isVisible = isVisible;
        this.direction = direction;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public AreaLink.CompassDirection getDirection() {
        return direction;
    }

}
