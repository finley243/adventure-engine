package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionCompare extends Expression {

    public enum Comparator {
        LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, NOT_EQUAL
    }

    private final Expression expression1;
    private final Expression expression2;
    private final Comparator comparator;

    public ExpressionCompare(Expression expression1, Expression expression2, Comparator comparator) {
        if (expression1 == null || expression2 == null) throw new IllegalArgumentException("One or more expressions is null");
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.comparator = comparator;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (!expression1.canCompareTo(expression2, context)) throw new IllegalArgumentException("Expressions cannot be compared");
        if ((expression1.getDataType(context) == Expression.DataType.INTEGER || expression1.getDataType(context) == Expression.DataType.FLOAT) &&
                (expression2.getDataType(context) == Expression.DataType.INTEGER || expression2.getDataType(context) == Expression.DataType.FLOAT)) {
            float value1;
            float value2;
            if (expression1.getDataType(context) == Expression.DataType.INTEGER) {
                value1 = expression1.getValueInteger(context);
            } else {
                value1 = expression1.getValueFloat(context);
            }
            if (expression2.getDataType(context) == Expression.DataType.INTEGER) {
                value2 = expression2.getValueInteger(context);
            } else {
                value2 = expression2.getValueFloat(context);
            }
            return comparatorCheckFloat(value1, value2, comparator);
        } else if (expression1.getDataType(context) == Expression.DataType.BOOLEAN) {
            if (comparator == Comparator.NOT_EQUAL) {
                return expression1.getValueBoolean(context) != expression2.getValueBoolean(context);
            }
            return expression1.getValueBoolean(context) == expression2.getValueBoolean(context);
        } else if (expression1.getDataType(context) == Expression.DataType.STRING) {
            if (comparator == Comparator.NOT_EQUAL) {
                return !expression1.getValueString(context).equals(expression2.getValueString(context));
            }
            return expression1.getValueString(context).equals(expression2.getValueString(context));
        } else if (expression1.getDataType(context) == Expression.DataType.STRING_SET) {
            if (comparator == Comparator.NOT_EQUAL) {
                return !expression1.getValueStringSet(context).equals(expression2.getValueStringSet(context));
            }
            return expression1.getValueStringSet(context).equals(expression2.getValueStringSet(context));
        }
        return false;
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
