package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptAdd extends Script {

    private final Script firstScript;
    private final Script secondScript;

    public ScriptAdd(Script firstScript, Script secondScript) {
        this.firstScript = firstScript;
        this.secondScript = secondScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData firstReturn = firstScript.execute(context);
        if (firstReturn.error() != null) {
            return firstReturn;
        } else if (firstReturn.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        } else if (firstReturn.value() == null) {
            return new ScriptReturnData(null, false, false, "Expression did not receive a value");
        }
        ScriptReturnData secondReturn = secondScript.execute(context);
        if (secondReturn.error() != null) {
            return secondReturn;
        } else if (secondReturn.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        } else if (secondReturn.value() == null) {
            return new ScriptReturnData(null, false, false, "Expression did not receive a value");
        }
        if (!canAddExpressions(firstReturn.value(), secondReturn.value(), context)) {
            return new ScriptReturnData(null, false, false, "Expression received values that could not be added");
        }
        Expression addResult = addExpressions(firstReturn.value(), secondReturn.value(), context);
        return new ScriptReturnData(addResult, false, false, null);
    }

    private boolean canAddExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType() == Expression.DataType.INTEGER || firstExpression.getDataType() == Expression.DataType.FLOAT) {
            return secondExpression.getDataType() == Expression.DataType.INTEGER || secondExpression.getDataType() == Expression.DataType.FLOAT;
        } else if (firstExpression.getDataType() == Expression.DataType.STRING) {
            return secondExpression.getDataType() == Expression.DataType.STRING;
        }
        return false;
    }

    private Expression addExpressions(Expression firstExpression, Expression secondExpression, Context context) {
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
