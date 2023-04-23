package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;

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
    public int getValueInteger(ContextScript context) {
        return Math.round(expressionFloat.getValueFloat(context));
    }

}
