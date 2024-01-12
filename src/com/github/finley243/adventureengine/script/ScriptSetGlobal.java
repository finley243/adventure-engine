package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetGlobal extends Script {

    private final Expression globalID;
    private final Expression expression;

    public ScriptSetGlobal(Condition condition, Expression globalID, Expression expression) {
        super(condition);
        if (globalID == null) throw new IllegalArgumentException("ScriptSetGlobal globalID is null");
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (globalID.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetGlobal globalID is not a string");
        String globalIDValue = globalID.getValueString(context);
        Expression expressionConstant = Expression.convertToConstant(expression, context);
        context.game().data().setGlobalExpression(globalIDValue, expressionConstant);
        sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
    }

}
