package com.github.finley243.adventureengine.expression;

import java.util.Objects;
import java.util.Set;

public class SetExpression extends Expression {

    private final Set<Expression> value;

    SetExpression(Set<Expression> value) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SetExpression expression)) {
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
