package com.github.finley243.adventureengine.expression;

import java.util.Objects;

public class StringExpression extends Expression {

    private final String value;

    StringExpression(String value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public String getValueString() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StringExpression expression)) {
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
