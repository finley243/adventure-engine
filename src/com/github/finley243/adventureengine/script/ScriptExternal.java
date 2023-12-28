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
    protected void executeSuccess(RuntimeStack runtimeStack) {
        Context innerContext = new Context(runtimeStack.getContext());
        runtimeStack.addContext(innerContext, this);
        Script externalScript = runtimeStack.getContext().game().data().getScript(scriptID);
        externalScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturn scriptReturn) {
        runtimeStack.closeContext();
        if (scriptReturn.error() != null) {
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, scriptReturn.error()));
        } else if (scriptReturn.isReturn()) {
            sendReturn(runtimeStack, new ScriptReturn(scriptReturn.value(), false, false, null));
        } else if (scriptReturn.value() == null) {
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
        } else {
            // TODO - Replace with a check for the specified function return type, rather than simply checking if a value is present without a return statement
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, "Function has no return statement"));
        }
    }

}
