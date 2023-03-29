package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

public class ObjectComponentTemplateLink extends ObjectComponentTemplate {

    private final Condition condition;
    private final boolean isMovable;
    private final boolean isVisible;

    public ObjectComponentTemplateLink(Game game, String ID, boolean startEnabled, String name, Condition condition, boolean isMovable, boolean isVisible) {
        super(game, ID, startEnabled, name);
        this.condition = condition;
        this.isMovable = isMovable;
        this.isVisible = isVisible;
    }

    public Condition getCondition() {
        return condition;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public boolean isVisible() {
        return isVisible;
    }

}
