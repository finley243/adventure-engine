package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;

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
    public int getValueInteger(ContextScript context) {
        return expressionSet.getValueStringSet(context).size();
    }

}
