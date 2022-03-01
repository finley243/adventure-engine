package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorInCombat extends Condition {

    private final ActorReference actor;

    public ConditionActorInCombat(boolean invert, ActorReference actor) {
        super(invert);
        this.actor = actor;
    }

    @Override
    public boolean isMet(Actor subject) {
        return actor.getActor(subject).isInCombat() != invert;
    }

}
