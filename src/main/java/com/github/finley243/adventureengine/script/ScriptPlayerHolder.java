package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptPlayerHolder extends Script {

    public ScriptPlayerHolder(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        return new ScriptReturnData(Expression.valueHolder(scriptRuntime.getPlayer()), null, null);
    }

}
