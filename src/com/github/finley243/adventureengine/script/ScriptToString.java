package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptToString extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        String stringValue = valueExpression.toString();
        return new ScriptReturnData(Expression.constant(stringValue), null, null);
    }

}
