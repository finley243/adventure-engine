package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptExternal extends Script {

    private final String scriptID;

    public ScriptExternal(Condition condition, String scriptID) {
        super(condition);
        this.scriptID = scriptID;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        context.game().data().getScript(scriptID).execute(context);
    }
}
