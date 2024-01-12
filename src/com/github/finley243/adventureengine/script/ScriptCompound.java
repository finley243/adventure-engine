package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ScriptCompound extends Script implements ScriptReturnTarget {

    private final List<Script> subScripts;

    public ScriptCompound(List<Script> subScripts, boolean select) {
        this.subScripts = subScripts;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
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
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        if (scriptReturnData.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (runtimeStack.getIndex() >= subScripts.size()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
        } else {
            executeNextScript(runtimeStack);
        }
    }

}
