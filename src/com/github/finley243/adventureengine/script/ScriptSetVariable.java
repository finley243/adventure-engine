package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptSetVariable extends Script {

    private final Expression variableName;
    private final Expression variableValue;

    public ScriptSetVariable(Condition condition, Map<String, Expression> localParameters, Expression variableName, Expression variableValue) {
        super(condition, localParameters);
        if (variableName == null) throw new IllegalArgumentException("ScriptSetVariable variableName is null");
        if (variableName.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetVariable variableName is not a string");
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    @Override
    protected void executeSuccess(Context context) {
        String variableNameString = variableName.getValueString(context);
        context.setParameter(variableNameString, variableValue);
    }

}
