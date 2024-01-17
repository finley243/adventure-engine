package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptSetVariable extends Script {

    private final String variableName;
    private final Script variableValue;
    private final boolean isDefinition;

    public ScriptSetVariable(String variableName, Script variableValue, boolean isDefinition) {
        if (variableName == null) throw new IllegalArgumentException("ScriptSetVariable variableName is null");
        this.variableName = variableName;
        this.variableValue = variableValue;
        this.isDefinition = isDefinition;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (isDefinition && context.getLocalVariables().containsKey(variableName)) {
            return new ScriptReturnData(null, null, "Variable with name is already defined");
        } else if (!isDefinition && !context.getLocalVariables().containsKey(variableName)) {
            return new ScriptReturnData(null, null, "Variable with name has not been defined");
        }
        ScriptReturnData valueResult = variableValue.execute(context);
        if (valueResult.error() != null) {
            return valueResult;
        } else if (valueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Expression cannot contain a flow statement");
        }
        context.setLocalVariable(variableName, valueResult.value());
        return new ScriptReturnData(null, null, null);
    }

}
