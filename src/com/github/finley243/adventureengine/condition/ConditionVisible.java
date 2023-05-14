package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ConditionVisible extends Condition {

    private final StatHolderReference actor;
    private final StatHolderReference target;

    public ConditionVisible(boolean invert, StatHolderReference actor, StatHolderReference target) {
        super(invert);
        this.actor = actor;
        this.target = target;
    }

    @Override
    public boolean isMetInternal(Context context) {
        if (!(actor.getHolder(context) instanceof Actor actorCast) || !(target.getHolder(context) instanceof Actor targetCast)) {
            return false;
        }
        return actorCast.canSee(targetCast);
    }
}
