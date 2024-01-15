package com.github.finley243.adventureengine.expression;

public class ExpressionSetContains extends Expression {

    private final Expression expressionSet;
    private final Expression expressionString;

    public ExpressionSetContains(Expression expressionSet, Expression expressionString) {
        if (expressionSet == null || expressionString == null) throw new IllegalArgumentException("One or more expressions is null");
        this.expressionSet = expressionSet;
        this.expressionString = expressionString;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean() {
        if (expressionSet.getDataType() != Expression.DataType.STRING_SET) throw new IllegalArgumentException("Expression expressionSet is not a string set");
        if (expressionString.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("Expression expressionString is not a string");
        return expressionSet.getValueStringSet().contains(expressionString.getValueString());
    }

}
