package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptIsVisible extends Script {

    @Override
    public Script.ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression targetExpression = context.getLocalVariables().get("target").getExpression();
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new Script.ScriptReturnData(null, false, false, "Actor parameter is not a stat holder");
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new Script.ScriptReturnData(null, false, false, "Actor parameter is not an actor");
        if (targetExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new Script.ScriptReturnData(null, false, false, "Target parameter is not a stat holder");
        if (!(targetExpression.getValueStatHolder() instanceof Actor target)) return new Script.ScriptReturnData(null, false, false, "Target parameter is not an actor");
        boolean isVisible = target.isVisible(actor) && actor.getLineOfSightActors().contains(target);
        return new Script.ScriptReturnData(Expression.constant(isVisible), false, false, null);
    }

}
