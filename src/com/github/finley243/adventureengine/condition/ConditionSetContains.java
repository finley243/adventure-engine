package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ConditionSetContains extends Condition {

    private final Expression expressionSet;
    private final Expression expressionString;

    public ConditionSetContains(boolean invert, Expression expressionSet, Expression expressionString) {
        super(invert);
        if (expressionSet == null || expressionString == null) throw new IllegalArgumentException("One or more variables is null");
        if (expressionSet.getDataType() != Expression.DataType.STRING_SET) throw new IllegalArgumentException("Variable is not a string set");
        if (expressionString.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("Variable is not a string");
        this.expressionSet = expressionSet;
        this.expressionString = expressionString;
    }

    @Override
    protected boolean isMetInternal(Context context) {
        return expressionSet.getValueStringSet(context).contains(expressionString.getValueString(context));
    }

}
