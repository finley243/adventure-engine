package com.github.finley243.adventureengine.expression;

public class ExpressionSetSize extends Expression {

    private final Expression expressionSet;

    public ExpressionSetSize(Expression expressionSet) {
        this.expressionSet = expressionSet;
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public int getValueInteger() {
        return expressionSet.getValueStringSet().size();
    }

}
