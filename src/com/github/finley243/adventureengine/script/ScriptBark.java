package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptBark extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression trigger = context.getLocalVariables().get("bark").getExpression();
        if (trigger.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, null, new ScriptErrorData("Bark parameter is not a string", -1));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", -1));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", -1));
        actor.triggerBark(trigger.getValueString(), context);
        return new ScriptReturnData(null, null, null);
    }

}
