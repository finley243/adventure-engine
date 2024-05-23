package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public class ScriptSetContains extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setExpression = context.getLocalVariables().get("set").getExpression();
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (setExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, "Set parameter is not a set");
        Set<Expression> set = setExpression.getValueSet();
        boolean setContainsValue = set.contains(valueExpression);
        return new ScriptReturnData(Expression.constant(setContainsValue), FlowStatementType.RETURN, null);
    }

}
