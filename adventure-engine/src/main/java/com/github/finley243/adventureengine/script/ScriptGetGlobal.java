package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetGlobal extends Script {

    private final Script globalName;

    public ScriptGetGlobal(ScriptTraceData traceData, Script globalName) {
        super(traceData);
        this.globalName = globalName;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptReturnData nameResult = globalName.execute(scriptRuntime, context);
        if (nameResult.error() != null) {
            return nameResult;
        } else if (nameResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        } else if (nameResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Global name expression returned null", getTraceData()));
        } else if (nameResult.value().getDataType() != Expression.DataType.STRING) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Global name must be a string", getTraceData()));
        }
        Expression globalValue = scriptRuntime.getGlobalExpression(nameResult.value().getValueString());
        return new ScriptReturnData(globalValue, null, null);
    }

}
