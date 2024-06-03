package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptReturn extends Script {

    private final Script scriptReturn;

    public ScriptReturn(int line, Script scriptReturn) {
        super(line);
        this.scriptReturn = scriptReturn;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (scriptReturn == null) {
            return new ScriptReturnData(null, FlowStatementType.RETURN, null);
        }
        ScriptReturnData scriptResult = scriptReturn.execute(context);
        if (scriptResult.error() != null) {
            return scriptResult;
        } else if (scriptResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getLine()));
        }
        return new ScriptReturnData(scriptResult.value(), FlowStatementType.RETURN, null);
    }

}
