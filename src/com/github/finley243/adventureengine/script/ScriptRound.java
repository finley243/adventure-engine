package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptRound extends Script {

    @Override
    public Script.ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (valueExpression.getDataType() != Expression.DataType.FLOAT) return new Script.ScriptReturnData(null, null, "Value parameter is not a float");
        float value = valueExpression.getValueFloat();
        int roundedValue = Math.round(value);
        return new Script.ScriptReturnData(Expression.constant(roundedValue), FlowStatementType.RETURN, null);
    }

}
