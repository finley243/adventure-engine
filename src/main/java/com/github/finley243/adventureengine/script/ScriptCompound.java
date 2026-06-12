package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;

    public ScriptCompound(ScriptTraceData traceData, List<Script> subScripts) {
        super(traceData);
        this.subScripts = subScripts;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Context innerContext = Context.from(context).build();
        for (Script subScript : subScripts) {
            ScriptReturnData result = subScript.execute(, innerContext);
            if (result.error() != null) {
                return result;
            } else if (result.flowStatement() != null) {
                return result;
            }
        }
        return new ScriptReturnData(null, null, null);
    }

}
