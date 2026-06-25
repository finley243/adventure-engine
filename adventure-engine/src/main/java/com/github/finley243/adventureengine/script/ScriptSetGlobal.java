package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetGlobal extends Script {

    private final Script globalName;
    private final Script globalValue;

    public ScriptSetGlobal(ScriptTraceData traceData, Script globalName, Script globalValue) {
        super(traceData);
        this.globalName = globalName;
        this.globalValue = globalValue;
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
        ScriptReturnData valueResult = globalValue.execute(scriptRuntime, context);
        if (valueResult.error() != null) {
            return valueResult;
        } else if (valueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        scriptRuntime.setGlobalExpression(nameResult.value().getValueString(), valueResult.value());
        return new ScriptReturnData(null, null, null);
    }

}
