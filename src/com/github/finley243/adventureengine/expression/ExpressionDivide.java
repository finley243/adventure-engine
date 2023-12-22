package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionDivide extends Expression {

    private final Expression expression1;
    private final Expression expression2;

    public ExpressionDivide(Expression expression1, Expression expression2) {
        if (expression1 == null || expression2 == null) throw new IllegalArgumentException("Null expression");
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public Expression.DataType getDataType() {
        return DataType.FLOAT;
    }

    @Override
    public float getValueFloat(Context context) {
        if (!(expression1.getDataType() == DataType.FLOAT || expression1.getDataType() == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionDivide");
        }
        if (!(expression2.getDataType() == DataType.FLOAT || expression2.getDataType() == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionDivide");
        }
        float value1;
        float value2;
        if (expression1.getDataType() == DataType.INTEGER) {
            value1 = expression1.getValueInteger(context);
        } else {
            value1 = expression1.getValueFloat(context);
        }
        if (expression2.getDataType() == DataType.INTEGER) {
            value2 = expression2.getValueInteger(context);
        } else {
            value2 = expression2.getValueFloat(context);
        }
        return value1 / value2;
    }

}
