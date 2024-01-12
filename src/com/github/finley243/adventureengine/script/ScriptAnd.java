package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptAnd extends Script implements ScriptReturnTarget {

    private final List<Script> subScripts;

    public ScriptAnd(List<Script> subScripts) {
        this.subScripts = subScripts;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        runtimeStack.addContext(runtimeStack.getContext(), this);
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
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression cannot contain a return statement"));
        } else if (scriptReturnData.value() == null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression did not receive a value"));
        } else if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.BOOLEAN) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression expected a boolean value"));
        } else if (!scriptReturnData.value().getValueBoolean(runtimeStack.getContext())) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(false), false, false, null));
        } else if (runtimeStack.getIndex() >= subScripts.size()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(true), false, false, null));
        } else {
            executeNextScript(runtimeStack);
        }
    }

}
