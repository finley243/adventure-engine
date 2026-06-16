package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

public class ScriptCopy extends Script {

    public ScriptCopy(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        Expression copyExpression = switch (valueExpression.getDataType()) {
            case LIST -> Expression.list(new ArrayList<>(valueExpression.getValueList()), Function.identity());
            case SET -> Expression.set(new HashSet<>(valueExpression.getValueSet()), Function.identity());
            default -> valueExpression;
        };
        return new ScriptReturnData(copyExpression, FlowStatementType.RETURN, null);
    }

}
