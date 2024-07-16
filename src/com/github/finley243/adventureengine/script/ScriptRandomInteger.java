package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.concurrent.ThreadLocalRandom;

public class ScriptRandomInteger extends Script {

    public ScriptRandomInteger(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression minExpression = context.getLocalVariables().get("min").getExpression();
        Expression maxExpression = context.getLocalVariables().get("max").getExpression();
        if (minExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Min parameter is not an integer", getTraceData()));
        if (maxExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Max parameter is not an integer", getTraceData()));
        int min = minExpression.getValueInteger();
        int max = maxExpression.getValueInteger();
        int result = ThreadLocalRandom.current().nextInt(min, max);
        return new ScriptReturnData(Expression.constant(result), FlowStatementType.RETURN, null);
    }

}
