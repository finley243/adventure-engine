package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.BehaviorTarget;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.List;

public abstract class Behavior {

    private final Condition condition;

    public Behavior(Condition condition) {
        this.condition = condition;
    }

    public boolean shouldTrigger(Actor subject) {
        return condition == null || condition.isMet(subject);
    }

    public abstract boolean actionIsTarget(Action action);

}
