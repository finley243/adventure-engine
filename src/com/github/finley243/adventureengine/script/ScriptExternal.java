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
        Set<String> validParameterNames = new HashSet<>();
        for (ScriptParser.ScriptParameter definitionParameter : script.parameters()) {
            validParameterNames.add(definitionParameter.name());
        }
        Set<String> providedParameterNames = new HashSet<>();
        for (ParameterContainer providedParameter : parameters) {
            if (!validParameterNames.contains(providedParameter.name())) {
                return new ScriptReturnData(null, false, false, "Function call has parameter that does not exist in function definition");
            }
            providedParameterNames.add(providedParameter.name());
            ScriptReturnData parameterValueResult = providedParameter.value().execute(context);
            if (parameterValueResult.error() != null) {
                return parameterValueResult;
            } else if (parameterValueResult.isReturn()) {
                return new ScriptReturnData(null, false, false, "Function parameter contains unexpected return statement");
            }
            innerContext.setLocalVariable(providedParameter.name(), parameterValueResult.value());
        }
        for (ScriptParser.ScriptParameter definitionParameter : script.parameters()) {
            if (!providedParameterNames.contains(definitionParameter.name())) {
                innerContext.setLocalVariable(definitionParameter.name(), definitionParameter.defaultValue());
            }
        }
        ScriptReturnData scriptResult = script.script().execute(context);
        if (!scriptResult.isReturn() && script.returnType() != null) {
            return new ScriptReturnData(null, false, false, "Function has non-void return type but is missing return statement");
        } else if (scriptResult.value() == null) {
            return new ScriptReturnData(null, false, false, null);
        } else if (script.returnType() == null) {
            return new ScriptReturnData(null, false, false, "Function is void but is returning an unexpected value");
        } else if (scriptResult.value().getDataType() == script.returnType()) {
            return new ScriptReturnData(scriptResult.value(), false, false, null);
        } else {
            return new ScriptReturnData(null, false, false, "Function return value does not match return type in function definition");
        }
    }

    public record ParameterContainer(String name, Script value) {}

}
