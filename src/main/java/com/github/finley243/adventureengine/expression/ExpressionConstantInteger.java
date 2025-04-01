package com.github.finley243.adventureengine.expression;

public class ExpressionConstantInteger extends Expression {

    private final int value;

    public ExpressionConstantInteger(int value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public int getValueInteger() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpressionConstantInteger expression)) {
            return false;
        } else {
            return expression.value == this.value;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
