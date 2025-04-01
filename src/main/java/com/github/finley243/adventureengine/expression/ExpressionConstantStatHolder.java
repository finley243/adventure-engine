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

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpressionConstantStatHolder expression)) {
            return false;
        } else {
            return expression.value == this.value;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
