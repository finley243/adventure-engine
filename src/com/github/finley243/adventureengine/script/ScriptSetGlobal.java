package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptSetGlobal extends Script {

    private final String globalID;
    private final Script globalValue;

    public ScriptSetGlobal(String globalID, Script globalValue) {
        if (globalID == null) throw new IllegalArgumentException("ScriptSetGlobal globalID is null");
        this.globalID = globalID;
        this.globalValue = globalValue;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData globalValueResult = globalValue.execute(context);
        if (globalValueResult.error() != null) {
            return globalValueResult;
        } else if (globalValueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Script expression contains unexpected flow statement");
        }
        context.game().data().setGlobalExpression(globalID, globalValueResult.value());
        return new ScriptReturnData(null, null, null);
    }

}
