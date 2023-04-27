package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;

public class ConditionRandom extends Condition {

    private final Expression chance;

    public ConditionRandom(boolean invert, Expression chance) {
        super(invert);
        if (chance == null) throw new IllegalArgumentException("ConditionRandom chance expression is null");
        if (chance.getDataType() != Expression.DataType.FLOAT) throw new IllegalArgumentException("ConditionRandom chance expression is not a float");
        this.chance = chance;
    }

    @Override
    public boolean isMetInternal(Context context) {
        return MathUtils.randomCheck(chance.getValueFloat(context));
    }

}
