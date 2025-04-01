package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSubString extends Script {

    public ScriptSubString(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression stringExpression = context.getLocalVariables().get("string").getExpression();
        Expression indexStartExpression = context.getLocalVariables().get("start").getExpression();
        Expression indexEndExpression = context.getLocalVariables().get("end").getExpression();
        if (stringExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("String parameter is not a string", getTraceData()));
        if (indexStartExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index start parameter is not an integer", getTraceData()));
        if (indexEndExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index end parameter is not an integer", getTraceData()));
        String stringValue = stringExpression.getValueString();
        String subString = stringValue.substring(indexStartExpression.getValueInteger(), indexEndExpression.getValueInteger());
        return new ScriptReturnData(Expression.constant(subString), FlowStatementType.RETURN, null);
    }

}
