package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetGlobal extends Script {

    private final String globalName;

    public ScriptGetGlobal(ScriptTraceData traceData, String globalName) {
        super(traceData);
        this.globalName = globalName;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression globalValue = scriptRuntime.getGlobalExpression(globalName);
        return new ScriptReturnData(globalValue, null, null);
    }

}
