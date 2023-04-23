package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ConditionBoolean extends Condition {

    private final Expression expression;

    public ConditionBoolean(boolean invert, Expression expression) {
        super(invert);
        if (expression == null) throw new IllegalArgumentException("Variable is null");
        if (expression.getDataType() != Expression.DataType.BOOLEAN) throw new IllegalArgumentException("Variable is non-boolean");
        this.expression = expression;
    }

    @Override
    protected boolean isMetInternal(Context context) {
        return expression.getValueBoolean(context);
    }

}
