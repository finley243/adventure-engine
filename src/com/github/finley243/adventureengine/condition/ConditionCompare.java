package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ConditionCompare extends Condition {

    private final Expression expression1;
    private final Expression expression2;
    private final Comparator comparator;

    public ConditionCompare(boolean invert, Expression expression1, Expression expression2, Comparator comparator) {
        super(invert);
        if (expression1 == null || expression2 == null) {
            throw new IllegalArgumentException("One or more variables is null");
        }
        if (!expression1.canCompareTo(expression2)) {
            throw new IllegalArgumentException("Variables cannot be compared");
        }
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.comparator = comparator;
    }

    @Override
    protected boolean isMetInternal(Context context) {
        if ((expression1.getDataType() == Expression.DataType.INTEGER || expression1.getDataType() == Expression.DataType.FLOAT) &&
            (expression2.getDataType() == Expression.DataType.INTEGER || expression2.getDataType() == Expression.DataType.FLOAT)) {
            float value1;
            float value2;
            if (expression1.getDataType() == Expression.DataType.INTEGER) {
                value1 = expression1.getValueInteger(context);
            } else {
                value1 = expression1.getValueFloat(context);
            }
            if (expression2.getDataType() == Expression.DataType.INTEGER) {
                value2 = expression2.getValueInteger(context);
            } else {
                value2 = expression2.getValueFloat(context);
            }
            return Condition.comparatorCheckFloat(value1, value2, comparator);
        } else if (expression1.getDataType() == Expression.DataType.BOOLEAN) {
            return expression1.getValueBoolean(context) == expression2.getValueBoolean(context);
        } else if (expression1.getDataType() == Expression.DataType.STRING) {
            return expression1.getValueString(context).equals(expression2.getValueString(context));
        } else if (expression1.getDataType() == Expression.DataType.STRING_SET) {
            return expression1.getValueStringSet(context).equals(expression2.getValueStringSet(context));
        }
        return false;
    }

}
