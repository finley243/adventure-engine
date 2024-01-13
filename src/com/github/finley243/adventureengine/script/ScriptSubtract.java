package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSubtract extends Script {

    private final Script firstScript;
    private final Script secondScript;

    public ScriptSubtract(Script firstScript, Script secondScript) {
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
        if (!canSubtractExpressions(firstReturn.value(), secondReturn.value(), context)) {
            return new ScriptReturnData(null, false, false, "Expression received values that could not be subtracted");
        }
        Expression subtractResult = subtractExpressions(firstReturn.value(), secondReturn.value(), context);
        return new ScriptReturnData(subtractResult, false, false, null);
    }

    private boolean canSubtractExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType(context) == Expression.DataType.INTEGER || firstExpression.getDataType(context) == Expression.DataType.FLOAT) {
            return secondExpression.getDataType(context) == Expression.DataType.INTEGER || secondExpression.getDataType(context) == Expression.DataType.FLOAT;
        }
        return false;
    }

    private Expression subtractExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType(context) == Expression.DataType.INTEGER && secondExpression.getDataType(context) == Expression.DataType.INTEGER) {
            int value1 = firstExpression.getValueInteger(context);
            int value2 = secondExpression.getValueInteger(context);
            return Expression.constant(value1 - value2);
        } else if ((firstExpression.getDataType(context) == Expression.DataType.INTEGER || firstExpression.getDataType(context) == Expression.DataType.FLOAT)
                && (secondExpression.getDataType(context) == Expression.DataType.INTEGER || secondExpression.getDataType(context) == Expression.DataType.FLOAT)) {
            float value1 = firstExpression.getDataType(context) == Expression.DataType.INTEGER ? firstExpression.getValueInteger(context) : firstExpression.getValueFloat(context);
            float value2 = secondExpression.getDataType(context) == Expression.DataType.INTEGER ? secondExpression.getValueInteger(context) : secondExpression.getValueFloat(context);
            return Expression.constant(value1 - value2);
        }
        return null;
    }

}
