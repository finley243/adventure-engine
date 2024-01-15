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
        if (!context.getLocalVariables().containsKey(variableName)) return new ScriptReturnData(null, false, false, "Specified variable has not been defined");
        Expression variableValue = context.getLocalVariables().get(variableName).getExpression();
        return new ScriptReturnData(variableValue, false, false, null);
    }

}
