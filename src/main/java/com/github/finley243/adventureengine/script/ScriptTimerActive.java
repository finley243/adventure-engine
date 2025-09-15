package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTimerActive extends Script {

    public ScriptTimerActive(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Game game, Context context) {
        Expression timerID = context.getLocalVariables().get("timer").getExpression();
        if (timerID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Timer parameter is not a string", getTraceData()));
        boolean timerIsActive = game.data().isTimerActive(timerID.getValueString());
        return new ScriptReturnData(Expression.constant(timerIsActive), FlowStatementType.RETURN, null);
    }

}
