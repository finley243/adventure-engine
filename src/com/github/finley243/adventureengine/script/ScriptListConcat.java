package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class ScriptListConcat extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression listOneExpression = context.getLocalVariables().get("listOne").getExpression();
        Expression listTwoExpression = context.getLocalVariables().get("listTwo").getExpression();
        if (listOneExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List one parameter is not a list", -1));
        if (listTwoExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List two parameter is not a list", -1));
        List<Expression> listOne = listOneExpression.getValueList();
        List<Expression> listTwo = listTwoExpression.getValueList();
        List<Expression> listCombined = new ArrayList<>(listOne);
        listCombined.addAll(listTwo);
        return new ScriptReturnData(Expression.constant(listCombined), FlowStatementType.RETURN, null);
    }

}
