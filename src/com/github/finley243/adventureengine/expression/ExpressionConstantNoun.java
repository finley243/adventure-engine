package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
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
    public Noun getValueNoun(Context context) {
        return value;
    }

}
