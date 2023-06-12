package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ExpressionMultiply extends Expression {

    private final List<Expression> expressions;
    private final boolean isFloat;

    public ExpressionMultiply(List<Expression> expressions) {
        if (expressions.isEmpty()) throw new IllegalArgumentException("Expression list is empty");
        this.expressions = expressions;
        boolean hasFloatVariable = false;
        for (Expression expression : expressions) {
            if (expression.getDataType() == DataType.FLOAT) {
                hasFloatVariable = true;
            } else if (expression.getDataType() != DataType.INTEGER) {
                throw new IllegalArgumentException("Non-numeric expression provided to ExpressionMultiply");
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
        int product = 1;
        for (Expression expression : expressions) {
            product *= expression.getValueInteger(context);
        }
        return product;
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        float product = 1.0f;
        for (Expression expression : expressions) {
            if (expression.getDataType() == DataType.FLOAT) {
                product *= expression.getValueFloat(context);
            } else {
                product *= expression.getValueInteger(context);
            }
        }
        return product;
    }

}
