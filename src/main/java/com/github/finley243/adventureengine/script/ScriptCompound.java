package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;

import java.util.List;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;

    public ScriptCompound(ScriptTraceData traceData, List<Script> subScripts) {
        super(traceData);
        this.subScripts = subScripts;
    }

    @Override
    public ScriptReturnData execute(Game game, Context context) {
        Context innerContext = new Context(context, true);
        for (Script subScript : subScripts) {
            ScriptReturnData result = subScript.execute(game, innerContext);
            if (result.error() != null) {
                return result;
            } else if (result.flowStatement() != null) {
                return result;
            }
        }
        return new ScriptReturnData(null, null, null);
    }

}
