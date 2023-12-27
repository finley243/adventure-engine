package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptExternal extends Script implements ScriptReturnTarget {

    private final String scriptID;

    public ScriptExternal(Condition condition, String scriptID) {
        super(condition);
        this.scriptID = scriptID;
    }

    @Override
    protected void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        Context innerContext = new Context(context);
        //context.game().eventQueue().addToFront(new ScriptEvent(context.game().data().getScript(scriptID), innerContext));
        Script externalScript = context.game().data().getScript(scriptID);
        externalScript.execute(innerContext, this);
    }

    @Override
    public void onScriptReturn(ScriptReturn scriptReturn) {
        if (scriptReturn.error() != null) {
            sendReturn(new ScriptReturn(null, false, false, scriptReturn.error()));
        } else if (scriptReturn.isReturn()) {
            sendReturn(new ScriptReturn(scriptReturn.value(), false, false, null));
        } else if (scriptReturn.value() == null) {
            sendReturn(new ScriptReturn(null, false, false, null));
        } else {
            // TODO - Replace with a check for the specified function return type, rather than simply checking if a value is present without a return statement
            sendReturn(new ScriptReturn(null, false, false, "Function has no return statement"));
        }
    }

}
