package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

import java.util.Set;

public class ExpressionRandomStringFromSet extends Expression {

    private final Expression stringSetVar;

    public ExpressionRandomStringFromSet(Expression stringSetVar) {
        this.stringSetVar = stringSetVar;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    public String getValueString(Context context) {
        Set<String> set = stringSetVar.getValueStringSet(context);
        if (set.isEmpty()) throw new UnsupportedOperationException("Provided string set is empty");
        return MathUtils.selectRandomFromSet(set);
    }

}
