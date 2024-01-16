package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptRandomBoolean extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression chanceExpression = context.getLocalVariables().get("chance").getExpression();
        if (chanceExpression.getDataType() != Expression.DataType.FLOAT) return new ScriptReturnData(null, false, false, "Chance parameter is not a float");
        float chance = chanceExpression.getValueFloat();
        if (chance < 0.0f || chance > 1.0f) return new ScriptReturnData(null, false, false, "Chance parameter is outside the valid range");
        boolean result = MathUtils.randomCheck(chance);
        return new ScriptReturnData(Expression.constant(result), false, false, null);
    }

}
