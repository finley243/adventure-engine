package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class ScriptBuildList extends Script {

    private final List<Script> valueScripts;

    public ScriptBuildList(ScriptTraceData traceData, List<Script> valueScripts) {
        super(traceData);
        this.valueScripts = valueScripts;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        List<Expression> computedValues = new ArrayList<>();
        for (Script valueScript : valueScripts) {
            ScriptReturnData scriptResult = valueScript.execute(context);
            if (scriptResult.error() != null) {
                return scriptResult;
            } else if (scriptResult.flowStatement() != null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("List expression contains unexpected flow statement", getTraceData()));
            } else if (scriptResult.value() == null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("List expression is null", getTraceData()));
            } else {
                computedValues.add(scriptResult.value());
            }
        }
        return new ScriptReturnData(Expression.constant(computedValues), null, null);
    }

}
