package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTargetType extends Script {

    public ScriptTargetType(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression targetExpression = context.getLocalVariables().get("target").getExpression();
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getTraceData()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getTraceData()));
        if (targetExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Target parameter is not a stat holder", getTraceData()));
        if (!(targetExpression.getValueStatHolder() instanceof Actor target)) return new ScriptReturnData(null, null, new ScriptErrorData("Target parameter is not an actor", getTraceData()));
        String targetType = actor.getTargetingComponent().getTargetType(target).toString().toLowerCase();
        return new ScriptReturnData(Expression.constant(targetType), FlowStatementType.RETURN, null);
    }

}
