package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public class ScriptRandomValueFromSet extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setExpression = context.getLocalVariables().get("set").getExpression();
        if (setExpression.getDataType() != Expression.DataType.STRING_SET) return new ScriptReturnData(null, false, false, "Set parameter is not a set");
        Set<String> set = setExpression.getValueStringSet();
        String selectedValue = MathUtils.selectRandomFromSet(set);
        return new ScriptReturnData(Expression.constant(selectedValue), false, false, null);
    }

}
