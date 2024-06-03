package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class ScriptListCopy extends Script {

    public ScriptListCopy(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listExpression = context.getLocalVariables().get("list").getExpression();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List parameter is not a list", getTraceData()));
        List<Expression> list = listExpression.getValueList();
        List<Expression> listCopy = new ArrayList<>(list);
        return new ScriptReturnData(Expression.constant(listCopy), FlowStatementType.RETURN, null);
    }

}
