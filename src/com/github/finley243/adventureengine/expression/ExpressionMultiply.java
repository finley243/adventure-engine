package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ExpressionMultiply extends Expression {

    private final List<Expression> expressions;
    private Boolean isFloat;

    public ExpressionMultiply(List<Expression> expressions) {
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
                    throw new IllegalArgumentException("Non-numeric expression provided to ExpressionMultiply");
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
        int product = 1;
        for (Expression expression : expressions) {
            product *= expression.getValueInteger(context);
        }
        return product;
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        float product = 1.0f;
        for (Expression expression : expressions) {
            if (expression.getDataType(context) == DataType.FLOAT) {
                product *= expression.getValueFloat(context);
            } else {
                product *= expression.getValueInteger(context);
            }
        }
        return product;
    }

}
