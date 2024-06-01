package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptSubList extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        Expression indexStartExpression = context.getLocalVariables().get("start").getExpression();
        Expression indexEndExpression = context.getLocalVariables().get("end").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, "List parameter is not a list");
        if (indexStartExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, "Index start parameter is not an integer");
        if (indexEndExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, "Index end parameter is not an integer");
        List<Expression> list = listExpression.getValueList();
        List<Expression> subList = list.subList(indexStartExpression.getValueInteger(), indexEndExpression.getValueInteger());
        return new ScriptReturnData(Expression.constant(subList), FlowStatementType.RETURN, null);
    }

}
