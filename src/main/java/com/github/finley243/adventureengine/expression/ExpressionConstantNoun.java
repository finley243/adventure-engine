package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.textgen.Noun;

public class ExpressionConstantNoun extends Expression {

    private final Noun value;

    public ExpressionConstantNoun(Noun value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.NOUN;
    }

    @Override
    public Noun getValueNoun() {
        return value;
    }

    @Override
    public String toString() {
        return value.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpressionConstantNoun expression)) {
            return false;
        } else {
            return expression.value == this.value;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
