package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionTimerActive extends Expression {

    private final Expression timerID;

    public ExpressionTimerActive(Expression timerID) {
        if (timerID == null) throw new IllegalArgumentException("ExpressionTimerActive timerID is null");
        if (timerID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ExpressionTimerActive timerID is not a string");
        this.timerID = timerID;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return context.game().data().isTimerActive(timerID.getValueString(context));
    }

}
