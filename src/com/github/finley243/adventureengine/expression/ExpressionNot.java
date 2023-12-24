package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionNot extends Expression {

    private final Expression expression;

    public ExpressionNot(Expression expression) {
        if (expression == null) throw new IllegalArgumentException("ExpressionNot expression is null");
        this.expression = expression;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (expression.getDataType(context) != DataType.BOOLEAN) throw new IllegalArgumentException("ExpressionNot expression is not a boolean");
        return !expression.getValueBoolean(context);
    }

}
