package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptListIndexGet extends Script {

    public ScriptListIndexGet(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        Expression indexExpression = context.getLocalVariables().get("index").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List parameter is not a list", getLine()));
        if (indexExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index parameter is not an integer", getLine()));
        List<Expression> list = listExpression.getValueList();
        int index = indexExpression.getValueInteger();
        if (index < 0 || index >= list.size()) return new ScriptReturnData(null, null, new ScriptErrorData("Index is out of bounds", getLine()));
        Expression valueAtIndex = list.get(index);
        return new ScriptReturnData(valueAtIndex, FlowStatementType.RETURN, null);
    }

}
