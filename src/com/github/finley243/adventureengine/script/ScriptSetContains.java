package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public class ScriptSetContains extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setExpression = context.getLocalVariables().get("set").getExpression();
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (setExpression.getDataType() != Expression.DataType.STRING_SET) return new ScriptReturnData(null, null, "Set parameter is not a set");
        if (valueExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Value parameter is not a string");
        Set<String> set = setExpression.getValueStringSet();
        String value = valueExpression.getValueString();
        boolean setContainsValue = set.contains(value);
        return new ScriptReturnData(Expression.constant(setContainsValue), null, null);
    }

}
