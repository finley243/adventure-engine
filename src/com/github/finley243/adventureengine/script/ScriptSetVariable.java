package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptSetVariable extends Script {

    private final String variableName;
    private final Script variableValue;

    public ScriptSetVariable(String variableName, Script variableValue) {
        if (variableName == null) throw new IllegalArgumentException("ScriptSetVariable variableName is null");
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData valueResult = variableValue.execute(context);
        if (valueResult.error() != null) {
            return valueResult;
        } else if (valueResult.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        }
        context.setLocalVariable(variableName, valueResult.value());
        return new ScriptReturnData(null, false, false, null);
    }

}
