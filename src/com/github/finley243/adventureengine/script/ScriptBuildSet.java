package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScriptBuildSet extends Script {

    private final List<Script> valueScripts;

    public ScriptBuildSet(int line, List<Script> valueScripts) {
        super(line);
        this.valueScripts = valueScripts;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Set<Expression> computedValues = new HashSet<>();
        for (Script valueScript : valueScripts) {
            ScriptReturnData scriptResult = valueScript.execute(context);
            if (scriptResult.error() != null) {
                return scriptResult;
            } else if (scriptResult.flowStatement() != null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Set expression contains unexpected flow statement", getLine()));
            } else if (scriptResult.value() == null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Set expression is null", getLine()));
            } else {
                computedValues.add(scriptResult.value());
            }
        }
        return new ScriptReturnData(Expression.constant(computedValues), null, null);
    }

}
