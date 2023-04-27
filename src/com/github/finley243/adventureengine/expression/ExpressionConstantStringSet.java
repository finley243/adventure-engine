package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.Set;

public class ExpressionConstantStringSet extends Expression {

    private final Set<String> value;

    public ExpressionConstantStringSet(Set<String> value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING_SET;
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        return value;
    }

}
