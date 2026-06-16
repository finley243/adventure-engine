package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptStatHolder extends Script {

    private final ScriptValueHolderReference scriptValueHolderReference;

    public ScriptStatHolder(ScriptTraceData traceData, ScriptValueHolderReference scriptValueHolderReference) {
        super(traceData);
        this.scriptValueHolderReference = scriptValueHolderReference;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptValueHolder statHolder = scriptValueHolderReference.getHolder(context);
        return new ScriptReturnData(Expression.valueHolder(statHolder), null, null);
    }

}
