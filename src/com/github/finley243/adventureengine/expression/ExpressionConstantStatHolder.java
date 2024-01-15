package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.stat.StatHolder;

public class ExpressionConstantStatHolder extends Expression {

    private final StatHolder value;

    public ExpressionConstantStatHolder(StatHolder value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.STAT_HOLDER;
    }

    @Override
    public StatHolder getValueStatHolder() {
        return value;
    }

}
