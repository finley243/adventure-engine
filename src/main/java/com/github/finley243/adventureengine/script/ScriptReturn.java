package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;

public class ScriptReturn extends Script {

    private final Script scriptReturn;

    public ScriptReturn(ScriptTraceData traceData, Script scriptReturn) {
        super(traceData);
        this.scriptReturn = scriptReturn;
    }

    @Override
    public ScriptReturnData execute(Game game, Context context) {
        if (scriptReturn == null) {
            return new ScriptReturnData(null, FlowStatementType.RETURN, null);
        }
        ScriptReturnData scriptResult = scriptReturn.execute(game, context);
        if (scriptResult.error() != null) {
            return scriptResult;
        } else if (scriptResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        return new ScriptReturnData(scriptResult.value(), FlowStatementType.RETURN, null);
    }

}
