package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpressionSetFromStrings extends Expression {

    private final List<Expression> stringVars;

    public ExpressionSetFromStrings(List<Expression> stringVars) {
        this.stringVars = stringVars;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.STRING_SET;
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        Set<String> stringSet = new HashSet<>();
        for (Expression stringVar : stringVars) {
            stringSet.add(stringVar.getValueString(context));
        }
        return stringSet;
    }

}
