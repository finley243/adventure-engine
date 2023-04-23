package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorVisible extends Condition {

    private final ActorReference actor;
    private final ActorReference target;

    public ConditionActorVisible(boolean invert, ActorReference actor, ActorReference target) {
        super(invert);
        this.actor = actor;
        this.target = target;
    }

    @Override
    public boolean isMetInternal(Context context) {
        return actor.getActor(context).canSee(target.getActor(context));
    }
}
