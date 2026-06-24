package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptNegate extends Script {

    private final Script expression;

    public ScriptNegate(ScriptTraceData traceData, Script expression) {
        super(traceData);
        this.expression = expression;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptReturnData returnData = expression.execute(scriptRuntime, context);
        if (returnData.error() != null) {
            return returnData;
        } else if (returnData.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        } else if (returnData.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not receive a value", getTraceData()));
        }
        if (returnData.value().getDataType() == Expression.DataType.INTEGER) {
            int original = returnData.value().getValueInteger();
            Expression negatedInteger = Expression.integer(-original);
            return new ScriptReturnData(negatedInteger, null, null);
        } else if (returnData.value().getDataType() == Expression.DataType.FLOAT) {
            float original = returnData.value().getValueFloat();
            Expression negatedFloat = Expression.decimal(-original);
            return new ScriptReturnData(negatedFloat, null, null);
        } else {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression received a value that could not be negated", getTraceData()));
        }
    }

}
