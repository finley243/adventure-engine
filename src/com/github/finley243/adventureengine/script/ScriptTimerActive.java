package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTimerActive extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression timerID = context.getLocalVariables().get("timer").getExpression();
        if (timerID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Timer parameter is not a string");
        boolean timerIsActive = context.game().data().isTimerActive(timerID.getValueString());
        return new ScriptReturnData(Expression.constant(timerIsActive), FlowStatementType.RETURN, null);
    }

}
