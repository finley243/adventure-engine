package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ExpressionAdd extends Expression {

    private final List<Expression> expressions;
    private Boolean isFloat;

    public ExpressionAdd(List<Expression> expressions) {
        if (expressions.isEmpty()) throw new IllegalArgumentException("Expression list is empty");
        this.expressions = expressions;
    }

    @Override
    public DataType getDataType(Context context) {
        if (isFloat == null) {
            boolean hasFloatVariable = false;
            for (Expression expression : expressions) {
                if (expression.getDataType(context) == DataType.FLOAT) {
                    hasFloatVariable = true;
                } else if (expression.getDataType(context) != DataType.INTEGER) {
                    throw new IllegalArgumentException("Non-numeric expression provided to ExpressionAdd");
                }
            }
            isFloat = hasFloatVariable;
        }
        if (isFloat) {
            return DataType.FLOAT;
        } else {
            return DataType.INTEGER;
        }
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType(context) != DataType.INTEGER) throw new UnsupportedOperationException();
        int sum = 0;
        for (Expression expression : expressions) {
            sum += expression.getValueInteger(context);
        }
        return sum;
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        float sum = 0.0f;
        for (Expression expression : expressions) {
            if (expression.getDataType(context) == DataType.FLOAT) {
                sum += expression.getValueFloat(context);
            } else {
                sum += expression.getValueInteger(context);
            }
        }
        return sum;
    }

}
