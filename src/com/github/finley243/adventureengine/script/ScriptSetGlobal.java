package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetGlobal extends Script {

    private final Expression globalID;
    private final Expression expression;

    public ScriptSetGlobal(Expression globalID, Expression expression) {
        if (globalID == null) throw new IllegalArgumentException("ScriptSetGlobal globalID is null");
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (globalID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetGlobal globalID is not a string");
        String globalIDValue = globalID.getValueString();
        Expression expressionConstant = Expression.convertToConstant(expression);
        context.game().data().setGlobalExpression(globalIDValue, expressionConstant);
        return new ScriptReturnData(null, false, false, null);
    }

}
