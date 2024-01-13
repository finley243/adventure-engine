package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptReturn extends Script {

    private final Script scriptReturn;

    public ScriptReturn(Script scriptReturn) {
        this.scriptReturn = scriptReturn;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData scriptResult = scriptReturn.execute(context);
        if (scriptResult.error() != null) {
            return scriptResult;
        } else if (scriptResult.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        }
        return new ScriptReturnData(scriptResult.value(), true, false, null);
    }

}
