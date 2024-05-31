package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.load.ScriptParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScriptExternal extends Script {

    private final String scriptID;
    private final List<ParameterContainer> parameters;

    public ScriptExternal(String scriptID, List<ParameterContainer> parameters) {
        this.scriptID = scriptID;
        this.parameters = parameters;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Context innerContext = new Context(context, false);
        ScriptParser.ScriptData script = context.game().data().getScript(scriptID);
        if (script == null) return new ScriptReturnData(null, null, "Function does not exist");
        Set<String> definitionParameterNames = new HashSet<>();
        for (ScriptParser.ScriptParameter definitionParameter : script.parameters()) {
            definitionParameterNames.add(definitionParameter.name());
        }
        Set<String> providedParameterNames = new HashSet<>();
        boolean hasUsedNamedParameter = false;
        for (int i = 0; i < parameters.size(); i++) {
            ParameterContainer providedParameter = parameters.get(i);
            if (!script.allowExtraParameters() && !definitionParameterNames.contains(providedParameter.name())) {
                return new ScriptReturnData(null, null, "Function call has parameter that does not exist in function definition");
            }
            if (providedParameter.name() == null) {
                if (i < script.parameters().size() && !script.parameters().get(i).isRequired()) {
                    return new ScriptReturnData(null, null, "Function call has positional parameter after named parameter");
                }
                if (hasUsedNamedParameter) {
                    return new ScriptReturnData(null, null, "Function call has positional parameter after named parameter");
                }
                ScriptReturnData parameterValueResult = providedParameter.value().execute(context);
                if (parameterValueResult.error() != null) {
                    return parameterValueResult;
                } else if (parameterValueResult.flowStatement() != null) {
                    return new ScriptReturnData(null, null, "Function parameter contains unexpected flow statement");
                }
                innerContext.setLocalVariable(script.parameters().get(i).name(), parameterValueResult.value());
                providedParameterNames.add(script.parameters().get(i).name());
            } else {
                if (!script.allowExtraParameters() && !definitionParameterNames.contains(providedParameter.name())) {
                    return new ScriptReturnData(null, null, "Function call has named parameter that does not exist in function definition");
                }
                ScriptReturnData parameterValueResult = providedParameter.value().execute(context);
                if (parameterValueResult.error() != null) {
                    return parameterValueResult;
                } else if (parameterValueResult.flowStatement() != null) {
                    return new ScriptReturnData(null, null, "Function parameter contains unexpected flow statement");
                }
                innerContext.setLocalVariable(providedParameter.name(), parameterValueResult.value());
                hasUsedNamedParameter = true;
                providedParameterNames.add(providedParameter.name());
            }
        }
        // Set default values for parameters that are not provided, and throw error if required parameter is not provided
        for (ScriptParser.ScriptParameter definitionParameter : script.parameters()) {
            if (!providedParameterNames.contains(definitionParameter.name())) {
                if (definitionParameter.isRequired()) {
                    return new ScriptReturnData(null, null, "Function call is missing required parameter: " + definitionParameter.name());
                }
                innerContext.setLocalVariable(definitionParameter.name(), definitionParameter.defaultValue());
            }
        }
        ScriptReturnData scriptResult = script.script().execute(innerContext);
        if (scriptResult.flowStatement() != null && scriptResult.flowStatement() != FlowStatementType.RETURN) {
            return new ScriptReturnData(null, null, "Function contains unhandled flow statement");
        } else if (scriptResult.flowStatement() != FlowStatementType.RETURN && script.hasReturn()) {
            return new ScriptReturnData(null, null, "Function has return type but is missing return statement");
        } else if (scriptResult.value() == null) {
            return new ScriptReturnData(null, null, null);
        } else if (!script.hasReturn()) {
            return new ScriptReturnData(null, null, "Function has no return but is returning an unexpected value");
        } else if (script.returnType() == null || scriptResult.value().getDataType() == script.returnType()) {
            return new ScriptReturnData(scriptResult.value(), null, null);
        } else {
            return new ScriptReturnData(null, null, "Function return value does not match return type in function definition");
        }
    }

    public record ParameterContainer(String name, Script value) {}

}
