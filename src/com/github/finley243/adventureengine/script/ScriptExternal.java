package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ScriptExternal extends Script {

    private final String scriptID;

    public ScriptExternal(Condition condition, Map<String, Variable> localParameters, String scriptID) {
        super(condition, localParameters);
        this.scriptID = scriptID;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        context.game().data().getScript(scriptID).execute(context);
    }
}
