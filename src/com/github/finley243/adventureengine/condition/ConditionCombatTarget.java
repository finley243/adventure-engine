package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionCombatTarget implements Condition {

    private final ActorReference actor;
    private final ActorReference target;

    public ConditionCombatTarget(ActorReference actor, ActorReference target) {
        this.actor = actor;
        this.target = target;
    }

    @Override
    public boolean isMet(Actor subject) {
        return actor.getActor(subject).isCombatTarget(target.getActor(subject));
    }

    @Override
    public String getChoiceTag() {
        return null;
    }
}
