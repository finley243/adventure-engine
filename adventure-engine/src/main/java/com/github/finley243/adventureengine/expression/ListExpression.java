package com.github.finley243.adventureengine.expression;

import java.util.List;
import java.util.Objects;

public class ListExpression extends Expression {

    private final List<Expression> value;

    ListExpression(List<Expression> value) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ListExpression expression)) {
            return false;
        } else {
            return Objects.equals(expression.value, this.value);
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
