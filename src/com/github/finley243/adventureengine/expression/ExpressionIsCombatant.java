package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ExpressionIsCombatant extends Expression {

    private final StatHolderReference actor;
    private final StatHolderReference target;

    public ExpressionIsCombatant(StatHolderReference actor, StatHolderReference target) {
        this.actor = actor;
        this.target = target;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            context.game().log().print("ExpressionIsCombatant StatHolderReference actor is not an Actor");
            return false;
        }
        if (!(target.getHolder(context) instanceof Actor targetCast)) {
            context.game().log().print("ExpressionIsCombatant StatHolderReference target is not an Actor");
            return false;
        }
        return actorCast.getTargetingComponent().isTargetOfType(targetCast, TargetingComponent.DetectionState.HOSTILE);
    }

}
