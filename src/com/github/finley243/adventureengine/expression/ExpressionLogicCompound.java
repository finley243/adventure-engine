package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ExpressionLogicCompound extends Expression {

    private final List<Expression> expressions;
    // If true, use AND operator
    // If false, use OR operator
    private final boolean useAnd;

    public ExpressionLogicCompound(List<Expression> expressions, boolean useAnd) {
        if (expressions.isEmpty()) throw new IllegalArgumentException("Expression list is empty");
        this.expressions = expressions;
        this.useAnd = useAnd;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        for (Expression expression : expressions) {
            if (expression.getDataType() != DataType.BOOLEAN) throw new IllegalArgumentException("Non-boolean expression provided to ExpressionLogicCompound");
            if (expression.getValueBoolean(context) != useAnd) {
                return !useAnd;
            }
        }
        return useAnd;
    }

}
