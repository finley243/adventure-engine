package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptComparator extends Script {

    public enum Comparator {
        LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, NOT_EQUAL
    }

    private final Script firstScript;
    private final Script secondScript;
    private final Comparator comparator;

    public ScriptComparator(Script firstScript, Script secondScript, Comparator comparator) {
        this.firstScript = firstScript;
        this.secondScript = secondScript;
        this.comparator = comparator;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData firstReturn = firstScript.execute(context);
        if (firstReturn.error() != null) {
            return firstReturn;
        } else if (firstReturn.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Expression cannot contain a return statement");
        }
        ScriptReturnData secondReturn = secondScript.execute(context);
        if (secondReturn.error() != null) {
            return secondReturn;
        } else if (secondReturn.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Expression cannot contain a return statement");
        }
        if (firstReturn.value() == null || secondReturn.value() == null) {
            if (comparator != Comparator.EQUAL && comparator != Comparator.NOT_EQUAL) {
                return new ScriptReturnData(null, null, "Expression has invalid comparator for null value");
            }
            Expression compareNullResult = Expression.constant(compareExpressionsNull(firstReturn.value(), secondReturn.value()));
            return new ScriptReturnData(compareNullResult, null, null);
        }
        if (!firstReturn.value().canCompareTo(secondReturn.value())) {
            return new ScriptReturnData(null, null, "Expression received values that could not be compared");
        }
        Expression compareResult = Expression.constant(compareExpressions(firstReturn.value(), secondReturn.value()));
        return new ScriptReturnData(compareResult, null, null);
    }

    private boolean compareExpressions(Expression expression1, Expression expression2) {
        if ((expression1.getDataType() == Expression.DataType.INTEGER || expression1.getDataType() == Expression.DataType.FLOAT) &&
                (expression2.getDataType() == Expression.DataType.INTEGER || expression2.getDataType() == Expression.DataType.FLOAT)) {
            float value1;
            float value2;
            if (expression1.getDataType() == Expression.DataType.INTEGER) {
                value1 = expression1.getValueInteger();
            } else {
                value1 = expression1.getValueFloat();
            }
            if (expression2.getDataType() == Expression.DataType.INTEGER) {
                value2 = expression2.getValueInteger();
            } else {
                value2 = expression2.getValueFloat();
            }
            return comparatorCheckFloat(value1, value2, comparator);
        } else if (expression1.getDataType() == Expression.DataType.BOOLEAN) {
            if (comparator == Comparator.NOT_EQUAL) {
                return expression1.getValueBoolean() != expression2.getValueBoolean();
            }
            return expression1.getValueBoolean() == expression2.getValueBoolean();
        } else if (expression1.getDataType() == Expression.DataType.STRING) {
            if (comparator == Comparator.NOT_EQUAL) {
                return !expression1.getValueString().equals(expression2.getValueString());
            }
            return expression1.getValueString().equals(expression2.getValueString());
        } else if (expression1.getDataType() == Expression.DataType.SET) {
            if (comparator == Comparator.NOT_EQUAL) {
                return !expression1.getValueSet().equals(expression2.getValueSet());
            }
            return expression1.getValueSet().equals(expression2.getValueSet());
        }
        return false;
    }

    private boolean compareExpressionsNull(Expression expression1, Expression expression2) {
        if (comparator == Comparator.EQUAL) {
            return expression1 == null && expression2 == null;
        } else {
            return expression1 != null || expression2 != null;
        }
    }

    private boolean comparatorCheckFloat(float value1, float value2, Comparator comparator) {
        return switch (comparator) {
            case LESS -> (value1 < value2);
            case GREATER -> (value1 > value2);
            case LESS_EQUAL -> (value1 <= value2);
            case EQUAL -> (value1 == value2);
            case NOT_EQUAL -> (value1 != value2);
            default -> (value1 >= value2);
        };
    }

}
