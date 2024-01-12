package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;

public class ScriptNot extends Script implements ScriptReturnTarget {

    private final Script subScript;

    public ScriptNot(Script subScript) {
        this.subScript = subScript;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        runtimeStack.addContext(runtimeStack.getContext(), this);
        subScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        runtimeStack.closeContext();
        if (scriptReturnData.error() != null) {
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression cannot contain a return statement"));
        } else if (scriptReturnData.value() == null) {
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression did not receive a value"));
        } else if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.BOOLEAN) {
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression expected a boolean value"));
        } else {
            Expression value = Expression.constant(!scriptReturnData.value().getValueBoolean(runtimeStack.getContext()));
            sendReturn(runtimeStack, new ScriptReturnData(value, false, false, null));
        }
    }

}
