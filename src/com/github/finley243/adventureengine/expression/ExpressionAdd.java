package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ExpressionAdd extends Expression {

    private final List<Expression> expressions;
    private final boolean isFloat;

    public ExpressionAdd(List<Expression> expressions) {
        if (expressions.isEmpty()) throw new IllegalArgumentException("Expression list is empty");
        this.expressions = expressions;
        boolean hasFloatVariable = false;
        for (Expression expression : expressions) {
            if (expression.getDataType() == DataType.FLOAT) {
                hasFloatVariable = true;
            } else if (expression.getDataType() != DataType.INTEGER) {
                throw new IllegalArgumentException("Non-numeric expression provided to ExpressionAdd");
            }
        }
        this.isFloat = hasFloatVariable;
    }

    @Override
    public DataType getDataType() {
        if (isFloat) {
            return DataType.FLOAT;
        } else {
            return DataType.INTEGER;
        }
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        int sum = 0;
        for (Expression expression : expressions) {
            sum += expression.getValueInteger(context);
        }
        return sum;
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        float sum = 0.0f;
        for (Expression expression : expressions) {
            if (expression.getDataType() == DataType.FLOAT) {
                sum += expression.getValueFloat(context);
            } else {
                sum += expression.getValueInteger(context);
            }
        }
        return sum;
    }

}