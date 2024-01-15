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

}
