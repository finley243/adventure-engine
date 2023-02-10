package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;
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
    public boolean isMetInternal(ContextScript context) {
        if(actor.getActor(context).targetingComponent() == null) return false;
        return actor.getActor(context).targetingComponent().isCombatant(target.getActor(context));
    }

}
