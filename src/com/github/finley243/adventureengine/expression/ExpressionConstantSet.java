package com.github.finley243.adventureengine.expression;

import java.util.Set;

public class ExpressionConstantSet extends Expression {

    private final Set<Expression> value;

    public ExpressionConstantSet(Set<Expression> value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.SET;
    }

    @Override
    public Set<Expression> getValueSet() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
