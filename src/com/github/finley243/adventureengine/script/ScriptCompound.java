package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.*;

public class ScriptCompound extends Script implements ScriptReturnTarget {

    private final List<Script> subScripts;

    public ScriptCompound(Condition condition, List<Script> subScripts, boolean select) {
        super(condition);
        this.subScripts = subScripts;
    }

    @Override
    public void executeSuccess(RuntimeStack runtimeStack) {
        Context innerContext = new Context(runtimeStack.getContext(), true);
        runtimeStack.addContext(innerContext, this);
        executeNextScript(runtimeStack);
    }

    private void executeNextScript(RuntimeStack runtimeStack) {
        Script currentScript = subScripts.get(runtimeStack.getIndex());
        runtimeStack.incrementIndex();
        currentScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturn scriptReturn) {
        if (scriptReturn.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (scriptReturn.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (runtimeStack.getIndex() >= subScripts.size()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
        } else {
            executeNextScript(runtimeStack);
        }
    }

}
