package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptListAdd extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, "List parameter is not a list");
        List<Expression> list = listExpression.getValueList();
        list.add(valueExpression);
        return new ScriptReturnData(null, null, null);
    }

}
