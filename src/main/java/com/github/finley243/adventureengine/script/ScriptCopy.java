package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.HashSet;

public class ScriptCopy extends Script {

    public ScriptCopy(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        Expression copyExpression = switch (valueExpression.getDataType()) {
            case LIST -> Expression.constant(new ArrayList<>(valueExpression.getValueList()));
            case SET -> Expression.constant(new HashSet<>(valueExpression.getValueSet()));
            default -> valueExpression;
        };
        return new ScriptReturnData(copyExpression, FlowStatementType.RETURN, null);
    }

}
