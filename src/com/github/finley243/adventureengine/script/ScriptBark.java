package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptBark extends Script {

    public ScriptBark(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression trigger = context.getLocalVariables().get("bark").getExpression();
        if (trigger.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, null, new ScriptErrorData("Bark parameter is not a string", getLine()));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getLine()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getLine()));
        actor.triggerBark(trigger.getValueString(), context);
        return new ScriptReturnData(null, null, null);
    }

}
