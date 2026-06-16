package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptIsVisible extends Script {

    public ScriptIsVisible(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    Script.ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression targetExpression = context.getLocalVariables().get("target").getExpression();
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new Script.ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getTraceData()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new Script.ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getTraceData()));
        if (targetExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new Script.ScriptReturnData(null, null, new ScriptErrorData("Target parameter is not a stat holder", getTraceData()));
        if (!(targetExpression.getValueStatHolder() instanceof Actor target)) return new Script.ScriptReturnData(null, null, new ScriptErrorData("Target parameter is not an actor", getTraceData()));
        boolean isVisible = target.isVisible(actor) && actor.getLineOfSightActors(context.game()).contains(target);
        return new Script.ScriptReturnData(Expression.bool(isVisible), FlowStatementType.RETURN, null);
    }

}
