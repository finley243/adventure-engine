package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class ScriptListCopy extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, "List parameter is not a list");
        List<Expression> list = listExpression.getValueList();
        List<Expression> listCopy = new ArrayList<>(list);
        return new ScriptReturnData(Expression.constant(listCopy), FlowStatementType.RETURN, null);
    }

}
