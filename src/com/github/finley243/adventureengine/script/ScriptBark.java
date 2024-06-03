package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptBark extends Script {

    public ScriptBark(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression trigger = context.getLocalVariables().get("bark").getExpression();
        if (trigger.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, null, new ScriptErrorData("Bark parameter is not a string", getTraceData()));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getTraceData()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getTraceData()));
        actor.triggerBark(trigger.getValueString(), context);
        return new ScriptReturnData(null, null, null);
    }

}
