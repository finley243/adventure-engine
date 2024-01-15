package com.github.finley243.adventureengine.expression;

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
    public int getValueInteger() {
        return Math.round(expressionFloat.getValueFloat());
    }

}
