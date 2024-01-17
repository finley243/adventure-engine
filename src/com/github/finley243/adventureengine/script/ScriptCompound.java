package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;

    public ScriptCompound(List<Script> subScripts) {
        this.subScripts = subScripts;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Context innerContext = new Context(context, true);
        for (Script subScript : subScripts) {
            ScriptReturnData result = subScript.execute(innerContext);
            if (result.error() != null) {
                return result;
            } else if (result.flowStatement() != null) {
                return result;
            }
        }
        return new ScriptReturnData(null, null, null);
    }

}
