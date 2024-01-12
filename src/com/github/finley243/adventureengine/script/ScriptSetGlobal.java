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
    public void execute(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (globalID.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetGlobal globalID is not a string");
        String globalIDValue = globalID.getValueString(context);
        Expression expressionConstant = Expression.convertToConstant(expression, context);
        context.game().data().setGlobalExpression(globalIDValue, expressionConstant);
        sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
    }

}
