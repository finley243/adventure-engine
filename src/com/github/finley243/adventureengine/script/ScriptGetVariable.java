package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetVariable extends Script {

    private final String variableName;

    public ScriptGetVariable(ScriptTraceData traceData, String variableName) {
        super(traceData);
        this.variableName = variableName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (!context.getLocalVariables().containsKey(variableName)) return new ScriptReturnData(null, null, new ScriptErrorData("Specified variable has not been defined: " + variableName, getTraceData()));
        Expression variableValue = context.getLocalVariables().get(variableName).getExpression();
        return new ScriptReturnData(variableValue, null, null);
    }

}
