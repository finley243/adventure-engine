package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

public class ExpressionRandomChance extends Expression {

    private final Expression chance;

    public ExpressionRandomChance(Expression chance) {
        if (chance == null) throw new IllegalArgumentException("ConditionRandom chance expression is null");
        if (chance.getDataType() != Expression.DataType.FLOAT) throw new IllegalArgumentException("ConditionRandom chance expression is not a float");
        this.chance = chance;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return MathUtils.randomCheck(chance.getValueFloat(context));
    }

}
