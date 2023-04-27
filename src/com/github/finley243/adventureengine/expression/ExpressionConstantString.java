package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionConstantString extends Expression {

    private final String value;

    public ExpressionConstantString(String value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public String getValueString(Context context) {
        return value;
    }

}
