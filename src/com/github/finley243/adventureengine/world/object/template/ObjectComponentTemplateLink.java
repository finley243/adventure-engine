package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

public class ObjectComponentTemplateLink extends ObjectComponentTemplate {

    private final Condition conditionMovable;
    private final Condition conditionVisible;
    private final boolean isMovable;
    private final boolean isVisible;

    public ObjectComponentTemplateLink(Game game, boolean startEnabled, boolean actionsRestricted, String name, Condition conditionMovable, Condition conditionVisible, boolean isMovable, boolean isVisible) {
        super(game, startEnabled, actionsRestricted, name);
        this.conditionMovable = conditionMovable;
        this.conditionVisible = conditionVisible;
        this.isMovable = isMovable;
        this.isVisible = isVisible;
    }

    public Condition getConditionMovable() {
        return conditionMovable;
    }

    public Condition getConditionVisible() {
        return conditionVisible;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public boolean isVisible() {
        return isVisible;
    }

}
