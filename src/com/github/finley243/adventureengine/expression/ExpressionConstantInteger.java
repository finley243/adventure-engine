package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionConstantInteger extends Expression {

    private final int value;

    public ExpressionConstantInteger(int value) {
        this.value = value;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.INTEGER;
    }

    @Override
    public int getValueInteger(Context context) {
        return value;
    }

}
