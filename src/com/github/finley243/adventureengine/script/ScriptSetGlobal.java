package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

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
        } else if (globalValueResult.isReturn()) {
            return new ScriptReturnData(null, false, false, "Script expression contains unexpected return statement");
        }
        context.game().data().setGlobalExpression(globalID, globalValueResult.value());
        return new ScriptReturnData(null, false, false, null);
    }

}
