package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionSetContains extends Expression {

    private final Expression expressionSet;
    private final Expression expressionString;

    public ExpressionSetContains(Expression expressionSet, Expression expressionString) {
        if (expressionSet == null || expressionString == null) throw new IllegalArgumentException("One or more expressions is null");
        this.expressionSet = expressionSet;
        this.expressionString = expressionString;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (expressionSet.getDataType(context) != Expression.DataType.STRING_SET) throw new IllegalArgumentException("Expression expressionSet is not a string set");
        if (expressionString.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("Expression expressionString is not a string");
        return expressionSet.getValueStringSet(context).contains(expressionString.getValueString(context));
    }

}
