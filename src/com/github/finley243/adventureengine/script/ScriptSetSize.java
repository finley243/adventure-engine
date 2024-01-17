package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public class ScriptSetSize extends Script {

    @Override
    public Script.ScriptReturnData execute(Context context) {
        Expression setExpression = context.getLocalVariables().get("set").getExpression();
        if (setExpression.getDataType() != Expression.DataType.STRING_SET) return new ScriptReturnData(null, null, "Set parameter is not a set");
        Set<String> set = setExpression.getValueStringSet();
        int setSize = set.size();
        return new ScriptReturnData(Expression.constant(setSize), null, null);
    }

}
