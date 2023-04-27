package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ConditionTimerActive extends Condition {

    private final Expression timerID;

    public ConditionTimerActive(boolean invert, Expression timerID) {
        super(invert);
        if (timerID == null) throw new IllegalArgumentException("ConditionTimerActive timerID expression is null");
        if (timerID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ConditionTimerActive timerID expression is not a string");
        this.timerID = timerID;
    }

    @Override
    protected boolean isMetInternal(Context context) {
        return context.game().data().isTimerActive(timerID.getValueString(context));
    }

}
