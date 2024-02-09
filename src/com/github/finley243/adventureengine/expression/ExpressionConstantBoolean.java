package com.github.finley243.adventureengine.expression;

public class ExpressionConstantBoolean extends Expression {

    private final boolean value;

    public ExpressionConstantBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpressionConstantBoolean expression)) {
            return false;
        } else {
            return expression.value == this.value;
        }
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

}
