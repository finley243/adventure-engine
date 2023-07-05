package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionTimeRange extends Expression {

    private final int hours1;
    private final int minutes1;
    private final int hours2;
    private final int minutes2;

    public ExpressionTimeRange(int hours1, int minutes1, int hours2, int minutes2) {
        this.hours1 = hours1;
        this.minutes1 = minutes1;
        this.hours2 = hours2;
        this.minutes2 = minutes2;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return context.game().data().time().isInRange(hours1, minutes1, hours2, minutes2);
    }

}
