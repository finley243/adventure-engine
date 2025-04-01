package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptSubList extends Script {

    public ScriptSubList(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        Expression indexStartExpression = context.getLocalVariables().get("start").getExpression();
        Expression indexEndExpression = context.getLocalVariables().get("end").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List parameter is not a list", getTraceData()));
        if (indexStartExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index start parameter is not an integer", getTraceData()));
        if (indexEndExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index end parameter is not an integer", getTraceData()));
        List<Expression> list = listExpression.getValueList();
        List<Expression> subList = list.subList(indexStartExpression.getValueInteger(), indexEndExpression.getValueInteger());
        return new ScriptReturnData(Expression.constant(subList), FlowStatementType.RETURN, null);
    }

}
