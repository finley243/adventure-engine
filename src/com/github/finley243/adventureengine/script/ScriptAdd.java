package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptAdd extends Script {

    private final Script firstScript;
    private final Script secondScript;

    public ScriptAdd(ScriptTraceData traceData, Script firstScript, Script secondScript) {
        super(traceData);
        this.firstScript = firstScript;
        this.secondScript = secondScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData firstReturn = firstScript.execute(context);
        if (firstReturn.error() != null) {
            return firstReturn;
        } else if (firstReturn.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        } else if (firstReturn.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not receive a value", getTraceData()));
        }
        ScriptReturnData secondReturn = secondScript.execute(context);
        if (secondReturn.error() != null) {
            return secondReturn;
        } else if (secondReturn.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        } else if (secondReturn.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not receive a value", getTraceData()));
        }
        if (!canAddExpressions(firstReturn.value(), secondReturn.value())) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression received values that could not be added", getTraceData()));
        }
        Expression addResult = addExpressions(firstReturn.value(), secondReturn.value());
        return new ScriptReturnData(addResult, null, null);
    }

    private boolean canAddExpressions(Expression firstExpression, Expression secondExpression) {
        if (firstExpression.getDataType() == Expression.DataType.INTEGER || firstExpression.getDataType() == Expression.DataType.FLOAT) {
            return secondExpression.getDataType() == Expression.DataType.INTEGER || secondExpression.getDataType() == Expression.DataType.FLOAT;
        } else if (firstExpression.getDataType() == Expression.DataType.STRING) {
            return secondExpression.getDataType() == Expression.DataType.STRING;
        }
        return false;
    }

    private Expression addExpressions(Expression firstExpression, Expression secondExpression) {
        if (firstExpression.getDataType() == Expression.DataType.INTEGER && secondExpression.getDataType() == Expression.DataType.INTEGER) {
            int value1 = firstExpression.getValueInteger();
            int value2 = secondExpression.getValueInteger();
            return Expression.constant(value1 + value2);
        } else if ((firstExpression.getDataType() == Expression.DataType.INTEGER || firstExpression.getDataType() == Expression.DataType.FLOAT)
                && (secondExpression.getDataType() == Expression.DataType.INTEGER || secondExpression.getDataType() == Expression.DataType.FLOAT)) {
            float value1 = firstExpression.getDataType() == Expression.DataType.INTEGER ? firstExpression.getValueInteger() : firstExpression.getValueFloat();
            float value2 = secondExpression.getDataType() == Expression.DataType.INTEGER ? secondExpression.getValueInteger() : secondExpression.getValueFloat();
            return Expression.constant(value1 + value2);
        } else if (firstExpression.getDataType() == Expression.DataType.STRING && secondExpression.getDataType() == Expression.DataType.STRING) {
            String value1 = firstExpression.getValueString();
            String value2 = secondExpression.getValueString();
            return Expression.constant(value1 + value2);
        }
        return null;
    }

}
