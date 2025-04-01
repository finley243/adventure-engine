package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptToString extends Script {

    public ScriptToString(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (valueExpression == null) {
            return new ScriptReturnData(Expression.constant("null"), FlowStatementType.RETURN, null);
        }
        String stringValue = valueExpression.toString();
        return new ScriptReturnData(Expression.constant(stringValue), FlowStatementType.RETURN, null);
    }

}
