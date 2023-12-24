package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionHasVariable extends Expression {

    private final String variableName;

    public ExpressionHasVariable(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return context.getLocalVariables().containsKey(variableName);
    }

}
