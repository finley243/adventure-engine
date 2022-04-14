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
    public boolean isMet(Actor subject) {
        if(actor.getActor(subject).targetingComponent() == null) return invert;
        return actor.getActor(subject).targetingComponent().isCombatant(target.getActor(subject)) != invert;
    }

}
