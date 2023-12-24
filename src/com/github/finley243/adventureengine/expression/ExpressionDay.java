package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionDay extends Expression {

    @Override
    public DataType getDataType(Context context) {
        return DataType.INTEGER;
    }

    @Override
    public int getValueInteger(Context context) {
        return context.game().data().dateTime().getDay();
    }

}
