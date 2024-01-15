package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptCombat extends Script{

    /*private final StatHolderReference actorReference;
    private final StatHolderReference targetReference;

    public ScriptCombat(StatHolderReference actorReference, StatHolderReference targetReference) {
        this.actorReference = actorReference;
        this.targetReference = targetReference;
    }*/

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression targetExpression = context.getLocalVariables().get("target").getExpression();
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, false, false, "Actor parameter is not a stat holder");
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, false, false, "Actor parameter is not an actor");
        if (targetExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, false, false, "Target parameter is not a stat holder");
        if (!(targetExpression.getValueStatHolder() instanceof Actor target)) return new ScriptReturnData(null, false, false, "Target parameter is not an actor");
        actor.getTargetingComponent().addCombatant(target);
        return new ScriptReturnData(null, false, false, null);
    }

}
