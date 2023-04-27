package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

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
    public float getValueFloat(Context context) {
        return value;
    }

}
