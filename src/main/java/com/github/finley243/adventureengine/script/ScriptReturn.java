package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptReturn extends Script {

    private final Script scriptReturn;

    public ScriptReturn(ScriptTraceData traceData, Script scriptReturn) {
        super(traceData);
        this.scriptReturn = scriptReturn;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        if (scriptReturn == null) {
            return new ScriptReturnData(null, FlowStatementType.RETURN, null);
        }
        ScriptReturnData scriptResult = scriptReturn.execute(scriptRuntime, context);
        if (scriptResult.error() != null) {
            return scriptResult;
        } else if (scriptResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        return new ScriptReturnData(scriptResult.value(), FlowStatementType.RETURN, null);
    }

}
