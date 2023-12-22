package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.List;

public class ExpressionConcatStrings extends Expression {

    private final List<Expression> stringExpressions;

    public ExpressionConcatStrings(List<Expression> stringExpressions) {
        this.stringExpressions = stringExpressions;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public String getValueString(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Expression expression : stringExpressions) {
            if (expression.getDataType() != DataType.STRING) throw new IllegalArgumentException("Non-string expression provided to ExpressionConcatStrings");
            stringBuilder.append(expression.getValueString(context));
        }
        return stringBuilder.toString();
    }

}
