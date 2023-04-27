package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

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
    public boolean getValueBoolean(Context context) {
        return value;
    }

}
