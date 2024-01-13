package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.ScriptParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScriptExternal extends Script implements ScriptReturnTarget {

    private final String scriptID;
    private final List<ParameterContainer> parameters;

    public ScriptExternal(String scriptID, List<ParameterContainer> parameters) {
        this.scriptID = scriptID;
        this.parameters = parameters;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        if (parameters.isEmpty()) {
            executeFunction(runtimeStack);
        } else {
            ScriptParser.ScriptData script = runtimeStack.getContext().game().data().getScript(scriptID);
            Set<String> validParameterNames = new HashSet<>();
            for (ScriptParser.ScriptParameter definitionParameter : script.parameters()) {
                validParameterNames.add(definitionParameter.name());
            }
            for (ParameterContainer providedParameter : parameters) {
                if (!validParameterNames.contains(providedParameter.name())) {
                    throw new IllegalArgumentException("Function call has parameter that does not exist in function definition");
                }
            }
            runtimeStack.addContext(runtimeStack.getContext(), this);
            computeNextParameter(runtimeStack);
        }
    }

    private void computeNextParameter(RuntimeStack runtimeStack) {
        Script currentParameterScript = parameters.get(runtimeStack.getIndex()).value();
        runtimeStack.incrementIndex();
        currentParameterScript.execute(runtimeStack);
    }

    private void executeFunction(RuntimeStack runtimeStack) {
        ScriptParser.ScriptData externalScript = runtimeStack.getContext().game().data().getScript(scriptID);
        Context innerContext = new Context(runtimeStack.getContext(), false);
        for (ScriptParser.ScriptParameter definitionParameter : externalScript.parameters()) {
            Expression parameterValue;
            if (!runtimeStack.getTempExpressionsFromMap(definitionParameter.name()).isEmpty()) {
                parameterValue = runtimeStack.getTempExpressionsFromMap(definitionParameter.name()).getFirst();
            } else {
                parameterValue = definitionParameter.defaultValue();
            }
            innerContext.setLocalVariable(definitionParameter.name(), parameterValue);
        }
        runtimeStack.addContext(innerContext, this);
        externalScript.script().execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        if (runtimeStack.getIndex() >= parameters.size()) {
            runtimeStack.closeContext();
            if (scriptReturnData.error() != null) {
                sendReturn(runtimeStack, scriptReturnData);
            } else {
                ScriptParser.ScriptData script = runtimeStack.getContext().game().data().getScript(scriptID);
                if (!scriptReturnData.isReturn() && script.returnType() != null) {
                    sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Function has non-void return type but is missing return statement"));
                } else if (scriptReturnData.value() == null && script.returnType() == null) {
                    sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
                } else if (scriptReturnData.value() != null && script.returnType() == null) {
                    sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Function is void but is returning an unexpected value"));
                } else if (scriptReturnData.value() != null && scriptReturnData.value().getDataType(runtimeStack.getContext()) == script.returnType()) {
                    sendReturn(runtimeStack, new ScriptReturnData(scriptReturnData.value(), false, false, null));
                } else {
                    sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Function return value does not match return type in function definition"));
                }
            }
        } else {
            if (scriptReturnData.error() != null) {
                runtimeStack.closeContext();
                sendReturn(runtimeStack, scriptReturnData);
            } else if (scriptReturnData.isReturn()) {
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Function contains unexpected return statement"));
            } else {
                String parameterName = parameters.get(runtimeStack.getIndex() - 1).name();
                runtimeStack.addTempExpressionToMap(parameterName, scriptReturnData.value());
                if (runtimeStack.getIndex() >= parameters.size()) {
                    runtimeStack.closeContext();
                    executeFunction(runtimeStack);
                } else {
                    computeNextParameter(runtimeStack);
                }
            }
        }
    }

    public record ParameterContainer(String name, Script value) {}

}
