package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetVariable extends Script {

    private final Expression variableName;
    private final Expression variableValue;

    public ScriptSetVariable(Condition condition, Expression variableName, Expression variableValue) {
        super(condition);
        if (variableName == null) throw new IllegalArgumentException("ScriptSetVariable variableName is null");
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    @Override
    protected void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        if (variableName.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetVariable variableName is not a string");
        String variableNameString = variableName.getValueString(context);
        Expression valueToConstant = Expression.convertToConstant(variableValue, context);
        context.setParameter(variableNameString, valueToConstant);
        sendReturn(new ScriptReturn(null, false, false, null));
    }

}
