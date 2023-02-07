package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ObjectComponentTemplateLink extends ObjectComponentTemplate {

    private final boolean isMovable;
    private final boolean isVisible;

    public ObjectComponentTemplateLink(Game game, String ID, boolean startEnabled, boolean isMovable, boolean isVisible) {
        super(game, ID, startEnabled);
        this.isMovable = isMovable;
        this.isVisible = isVisible;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public boolean isVisible() {
        return isVisible;
    }

}
