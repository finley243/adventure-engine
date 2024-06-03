package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptSetVariable extends Script {

    private final String variableName;
    private final Script variableValue;
    private final boolean isDefinition;

    public ScriptSetVariable(ScriptTraceData traceData, String variableName, Script variableValue, boolean isDefinition) {
        super(traceData);
        if (variableName == null) throw new IllegalArgumentException("ScriptSetVariable variableName is null");
        this.variableName = variableName;
        this.variableValue = variableValue;
        this.isDefinition = isDefinition;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (isDefinition && context.getLocalVariables().containsKey(variableName)) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Variable with name is already defined", getTraceData()));
        } else if (!isDefinition && !context.getLocalVariables().containsKey(variableName)) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Variable with name has not been defined", getTraceData()));
        }
        ScriptReturnData valueResult = variableValue.execute(context);
        if (valueResult.error() != null) {
            return valueResult;
        } else if (valueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        context.setLocalVariable(variableName, valueResult.value());
        return new ScriptReturnData(null, null, null);
    }

}
