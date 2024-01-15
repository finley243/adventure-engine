package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ExpressionIsVisible extends Expression {

    private final StatHolderReference actor;
    private final StatHolderReference target;

    public ExpressionIsVisible(StatHolderReference actor, StatHolderReference target) {
        this.actor = actor;
        this.target = target;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean() {
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            context.game().log().print("ExpressionIsVisible StatHolderReference actor is not an Actor");
            return false;
        }
        if (!(target.getHolder(context) instanceof Actor targetCast)) {
            context.game().log().print("ExpressionIsVisible StatHolderReference target is not an Actor");
            return false;
        }
        if (!targetCast.isVisible(actorCast)) {
            return false;
        }
        return actorCast.getLineOfSightActors().contains(targetCast);
    }

}
