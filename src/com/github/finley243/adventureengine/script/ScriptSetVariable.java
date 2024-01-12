package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetVariable extends Script {

    private final Expression variableName;
    private final Expression variableValue;

    public ScriptSetVariable(Expression variableName, Expression variableValue) {
        if (variableName == null) throw new IllegalArgumentException("ScriptSetVariable variableName is null");
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (variableName.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetVariable variableName is not a string");
        String variableNameString = variableName.getValueString(context);
        Expression valueToConstant = Expression.convertToConstant(variableValue, context);
        context.setLocalVariable(variableNameString, valueToConstant);
        sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
    }

}
