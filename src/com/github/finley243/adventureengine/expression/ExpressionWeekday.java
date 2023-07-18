package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionWeekday extends Expression {

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public String getValueString(Context context) {
        return context.game().data().dateTime().getWeekday();
    }

}
