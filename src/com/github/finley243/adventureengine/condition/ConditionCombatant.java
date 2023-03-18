package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;

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
        if(actor.getActor(context).getTargetingComponent() == null) return false;
        return actor.getActor(context).getTargetingComponent().isTargetOfType(target.getActor(context), TargetingComponent.DetectionState.HOSTILE);
    }

}
