package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptModifyGlobal extends Script {

    private final Expression globalID;
    private final Expression expression;

    public ScriptModifyGlobal(ScriptTraceData traceData, Expression globalID, Expression expression) {
        super(traceData);
        if (globalID == null) throw new IllegalArgumentException("ScriptModifyGlobal globalID is null");
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        if (globalID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptModifyGlobal globalID is not a string");
        String globalIDValue = globalID.getValueString();
        if (scriptRuntime.getGlobalExpression(globalIDValue).getDataType() != expression.getDataType()) throw new IllegalArgumentException("ScriptModifyGlobal expression data type does not match global");
        switch (expression.getDataType()) {
            case INTEGER -> {
                int oldValueInt = scriptRuntime.getGlobalExpression(globalIDValue).getValueInteger();
                scriptRuntime.setGlobalExpression(globalIDValue, Expression.integer(oldValueInt + expression.getValueInteger()));
            }
            case FLOAT -> {
                float oldValueFloat = scriptRuntime.getGlobalExpression(globalIDValue).getValueFloat();
                scriptRuntime.setGlobalExpression(globalIDValue, Expression.decimal(oldValueFloat + expression.getValueFloat()));
            }
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType());
        }
        return new ScriptReturnData(null, null, null);
    }

}
