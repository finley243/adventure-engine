package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorHP extends Condition {

    private final ActorReference actor;
    private final Equality equality;
    private final float value;

    public ConditionActorHP(boolean invert, ActorReference actor, Equality equality, float value) {
        super(invert);
        this.actor = actor;
        this.equality = equality;
        this.value = value;
    }

    @Override
    public boolean isMetInternal(Actor subject) {
        float healthProportion = actor.getActor(subject).getHPProportion();
        return Condition.equalityCheckFloat(healthProportion, value, equality);
    }

}
