package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScriptBuildSet extends Script {

    private final List<Script> valueScripts;

    public ScriptBuildSet(List<Script> valueScripts) {
        this.valueScripts = valueScripts;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Set<String> computedValues = new HashSet<>();
        for (Script valueScript : valueScripts) {
            ScriptReturnData scriptResult = valueScript.execute(context);
            if (scriptResult.error() != null) {
                return scriptResult;
            } else if (scriptResult.isReturn()) {
                return new ScriptReturnData(null, false, false, "Set expression contains unexpected return statement");
            } else if (scriptResult.value() == null) {
                return new ScriptReturnData(null, false, false, "Set expression is null");
            } else if (scriptResult.value().getDataType() != Expression.DataType.STRING) {
                return new ScriptReturnData(null, false, false, "Set expression is not a string");
            } else {
                computedValues.add(scriptResult.value().getValueString());
            }
        }
        return new ScriptReturnData(Expression.constant(computedValues), false, false, null);
    }

}
