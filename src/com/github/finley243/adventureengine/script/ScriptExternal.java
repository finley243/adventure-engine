package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptExternal extends Script implements ScriptReturnTarget {

    private final String scriptID;
    private final List<ParameterContainer> parameters;

    public ScriptExternal(String scriptID, List<ParameterContainer> parameters) {
        this.scriptID = scriptID;
        this.parameters = parameters;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        Context innerContext = new Context(runtimeStack.getContext(), false);
        for (ParameterContainer parameter : parameters) {
            innerContext.setLocalVariable(parameter.name(), Expression.convertToConstant(parameter.value(), runtimeStack.getContext()));
        }
        runtimeStack.addContext(innerContext, this);
        Script externalScript = runtimeStack.getContext().game().data().getScript(scriptID);
        externalScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        runtimeStack.closeContext();
        if (scriptReturnData.error() != null) {
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            sendReturn(runtimeStack, new ScriptReturnData(scriptReturnData.value(), false, false, null));
        } else if (scriptReturnData.value() == null) {
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
        } else {
            // TODO - Replace with a check for the specified function return type, rather than simply checking if a value is present without a return statement
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Function provides a value but has no return statement"));
        }
    }

    public record ParameterContainer(String name, Script value) {}

}
