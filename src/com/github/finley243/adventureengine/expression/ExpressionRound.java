package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionRound extends Expression {

    private final Expression expressionFloat;

    public ExpressionRound(Expression expressionFloat) {
        this.expressionFloat = expressionFloat;
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public int getValueInteger(Context context) {
        return Math.round(expressionFloat.getValueFloat(context));
    }

}
