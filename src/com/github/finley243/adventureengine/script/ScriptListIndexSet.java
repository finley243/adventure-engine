package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptListIndexSet extends Script {

    public ScriptListIndexSet(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        Expression indexExpression = context.getLocalVariables().get("index").getExpression();
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List parameter is not a list", getTraceData()));
        if (indexExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index parameter is not an integer", getTraceData()));
        List<Expression> list = listExpression.getValueList();
        int index = indexExpression.getValueInteger();
        if (index < 0 || index >= list.size()) return new ScriptReturnData(null, null, new ScriptErrorData("Index is out of bounds", getTraceData()));
        list.set(index, valueExpression);
        return new ScriptReturnData(null, null, null);
    }

}
