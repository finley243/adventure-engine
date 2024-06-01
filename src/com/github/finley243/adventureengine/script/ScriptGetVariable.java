package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetVariable extends Script {

    private final String variableName;

    public ScriptGetVariable(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (!context.getLocalVariables().containsKey(variableName)) return new ScriptReturnData(null, null, new ScriptErrorData("Specified variable has not been defined: " + variableName, -1));
        Expression variableValue = context.getLocalVariables().get(variableName).getExpression();
        return new ScriptReturnData(variableValue, null, null);
    }

}
