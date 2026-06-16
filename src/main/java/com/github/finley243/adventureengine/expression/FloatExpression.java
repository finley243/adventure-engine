package com.github.finley243.adventureengine.expression;

public class FloatExpression extends Expression {

    private final float value;

    FloatExpression(float value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.FLOAT;
    }

    @Override
    public float getValueFloat() {
        return value;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FloatExpression expression)) {
            return false;
        } else {
            return expression.value == this.value;
        }
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

}
