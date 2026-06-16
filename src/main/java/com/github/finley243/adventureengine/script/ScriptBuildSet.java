package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ScriptBuildSet extends Script {

    private final List<Script> valueScripts;

    public ScriptBuildSet(ScriptTraceData traceData, List<Script> valueScripts) {
        super(traceData);
        this.valueScripts = valueScripts;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Set<Expression> computedValues = new HashSet<>();
        for (Script valueScript : valueScripts) {
            ScriptReturnData scriptResult = valueScript.execute(, context);
            if (scriptResult.error() != null) {
                return scriptResult;
            } else if (scriptResult.flowStatement() != null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Set expression contains unexpected flow statement", getTraceData()));
            } else if (scriptResult.value() == null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Set expression is null", getTraceData()));
            } else {
                computedValues.add(scriptResult.value());
            }
        }
        return new ScriptReturnData(Expression.set(computedValues, Function.identity()), null, null);
    }

}
