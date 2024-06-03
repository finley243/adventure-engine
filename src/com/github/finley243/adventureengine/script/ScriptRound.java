package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptRound extends Script {

    public ScriptRound(int line) {
        super(line);
    }

    @Override
    public Script.ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (valueExpression.getDataType() != Expression.DataType.FLOAT) return new Script.ScriptReturnData(null, null, new ScriptErrorData("Value parameter is not a float", getLine()));
        float value = valueExpression.getValueFloat();
        int roundedValue = Math.round(value);
        return new Script.ScriptReturnData(Expression.constant(roundedValue), FlowStatementType.RETURN, null);
    }

}
