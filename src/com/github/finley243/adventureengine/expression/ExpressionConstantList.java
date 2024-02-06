package com.github.finley243.adventureengine.expression;

import java.util.List;

public class ExpressionConstantList extends Expression {

    private final List<Expression> value;

    public ExpressionConstantList(List<Expression> value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.LIST;
    }

    @Override
    public List<Expression> getValueList() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
