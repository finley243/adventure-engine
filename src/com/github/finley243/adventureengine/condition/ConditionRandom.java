package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;

public class ConditionRandom extends Condition {

    private final float chance;

    public ConditionRandom(boolean invert, float chance) {
        super(invert);
        this.chance = chance;
    }

    @Override
    public boolean isMetInternal(Actor subject) {
        return MathUtils.randomCheck(chance);
    }

}
