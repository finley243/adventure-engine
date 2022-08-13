package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionCombatant extends Condition {

    private final ActorReference actor;
    private final ActorReference target;

    public ConditionCombatant(boolean invert, ActorReference actor, ActorReference target) {
        super(invert);
        this.actor = actor;
        this.target = target;
    }

    @Override
    public boolean isMetInternal(Actor subject, Actor target) {
        if(actor.getActor(subject, target).targetingComponent() == null) return false;
        return actor.getActor(subject, target).targetingComponent().isCombatant(this.target.getActor(subject, target));
    }

}
