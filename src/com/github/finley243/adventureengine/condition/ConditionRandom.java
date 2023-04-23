package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

public class ConditionRandom extends Condition {

    private final float chance;

    public ConditionRandom(boolean invert, float chance) {
        super(invert);
        this.chance = chance;
    }

    @Override
    public boolean isMetInternal(Context context) {
        return MathUtils.randomCheck(chance);
    }

}
