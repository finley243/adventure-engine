package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionMonth extends Expression {

    @Override
    public Expression.DataType getDataType(Context context) {
        return Expression.DataType.INTEGER;
    }

    @Override
    public int getValueInteger(Context context) {
        return context.game().data().dateTime().getMonth();
    }

}
