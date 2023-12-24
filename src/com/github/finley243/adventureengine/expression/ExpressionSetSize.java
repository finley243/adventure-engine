package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionSetSize extends Expression {

    private final Expression expressionSet;

    public ExpressionSetSize(Expression expressionSet) {
        this.expressionSet = expressionSet;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.INTEGER;
    }

    @Override
    public int getValueInteger(Context context) {
        return expressionSet.getValueStringSet(context).size();
    }

}
