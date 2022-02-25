package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionCombatTarget extends Condition {

    private final ActorReference actor;
    private final ActorReference target;

    public ConditionCombatTarget(boolean invert, ActorReference actor, ActorReference target) {
        super(invert);
        this.actor = actor;
        this.target = target;
    }

    @Override
    public boolean isMet(Actor subject) {
        return actor.getActor(subject).isCombatTarget(target.getActor(subject)) != invert;
    }

}
