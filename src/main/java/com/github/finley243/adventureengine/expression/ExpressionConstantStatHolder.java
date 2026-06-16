package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.script.ScriptValueHolder;

public class ExpressionConstantStatHolder extends Expression {

    private final ScriptValueHolder value;

    public ExpressionConstantStatHolder(ScriptValueHolder value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.STAT_HOLDER;
    }

    @Override
    public ScriptValueHolder getValueStatHolder() {
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
