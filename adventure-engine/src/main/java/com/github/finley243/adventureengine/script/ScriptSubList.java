package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;
import java.util.function.Function;

public class ScriptSubList extends Script {

    public ScriptSubList(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        Expression indexStartExpression = context.getLocalVariables().get("start").getExpression();
        Expression indexEndExpression = context.getLocalVariables().get("end").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List parameter is not a list", getTraceData()));
        if (indexStartExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index start parameter is not an integer", getTraceData()));
        if (indexEndExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index end parameter is not an integer", getTraceData()));
        List<Expression> list = listExpression.getValueList();
        List<Expression> subList = list.subList(indexStartExpression.getValueInteger(), indexEndExpression.getValueInteger());
        return new ScriptReturnData(Expression.list(subList, Function.identity()), FlowStatementType.RETURN, null);
    }

}
