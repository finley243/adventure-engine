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
        for (int i = 0; i < subScripts.size(); i++) {
            Script subScript = subScripts.get(i);
            ScriptReturnData result = subScript.execute(innerContext);
            if (result.error() != null) {
                return new ScriptReturnData(null, null, new ScriptErrorData(result.error().message(), i + 1));
            } else if (result.flowStatement() != null) {
                return result;
            }
        }
        return new ScriptReturnData(null, null, null);
    }

}
