package com.github.finley243.adventureengine.expression;

public class ExpressionConstantFloat extends Expression {

    private final float value;

    public ExpressionConstantFloat(float value) {
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

}
