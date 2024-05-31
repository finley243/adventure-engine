package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public class ScriptSetRemove extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setExpression = context.getLocalVariables().get("set").getExpression();
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (setExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, "Set parameter is not a set");
        Set<Expression> set = setExpression.getValueSet();
        set.remove(valueExpression);
        return new ScriptReturnData(null, null, null);
    }

}
