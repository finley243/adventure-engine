package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionNot extends Expression {

    private final Expression expression;

    public ExpressionNot(Expression expression) {
        if (expression == null) throw new IllegalArgumentException("ExpressionNot expression is null");
        if (expression.getDataType() != DataType.BOOLEAN) throw new IllegalArgumentException("ExpressionNot expression is not a boolean");
        this.expression = expression;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return !expression.getValueBoolean(context);
    }

}
