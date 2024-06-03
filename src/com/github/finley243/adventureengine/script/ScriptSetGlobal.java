package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptSetGlobal extends Script {

    private final String globalName;
    private final Script globalValue;

    public ScriptSetGlobal(ScriptTraceData traceData, String globalName, Script globalValue) {
        super(traceData);
        if (globalName == null) throw new IllegalArgumentException("ScriptSetGlobal globalID is null");
        this.globalName = globalName;
        this.globalValue = globalValue;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData globalValueResult = globalValue.execute(context);
        if (globalValueResult.error() != null) {
            return globalValueResult;
        } else if (globalValueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Script expression contains unexpected flow statement", getTraceData()));
        }
        context.game().data().setGlobalExpression(globalName, globalValueResult.value());
        return new ScriptReturnData(null, null, null);
    }

}
