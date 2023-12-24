package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionSubtract extends Expression {

    private final Expression expression1;
    private final Expression expression2;

    public ExpressionSubtract(Expression expression1, Expression expression2) {
        if (expression1 == null || expression2 == null) throw new IllegalArgumentException("Null expression");
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public DataType getDataType(Context context) {
        if (!(expression1.getDataType(context) == DataType.FLOAT || expression1.getDataType(context) == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionSubtract");
        }
        if (!(expression2.getDataType(context) == DataType.FLOAT || expression2.getDataType(context) == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionSubtract");
        }
        if (expression1.getDataType(context) != DataType.INTEGER || expression2.getDataType(context) != DataType.INTEGER) {
            return DataType.FLOAT;
        } else {
            return DataType.INTEGER;
        }
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType(context) != DataType.INTEGER) throw new UnsupportedOperationException();
        int value1 = expression1.getValueInteger(context);
        int value2 = expression2.getValueInteger(context);
        return value1 - value2;
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        float value1;
        float value2;
        if (expression1.getDataType(context) == DataType.INTEGER) {
            value1 = expression1.getValueInteger(context);
        } else {
            value1 = expression1.getValueFloat(context);
        }
        if (expression2.getDataType(context) == DataType.INTEGER) {
            value2 = expression2.getValueInteger(context);
        } else {
            value2 = expression2.getValueFloat(context);
        }
        return value1 - value2;
    }

}
