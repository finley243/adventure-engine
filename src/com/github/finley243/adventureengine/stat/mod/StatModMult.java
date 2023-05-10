package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModMult extends StatMod {

    private final float mult;

    public StatModMult(Condition condition, float mult) {
        super(condition);
        this.mult = mult;
    }

    public float getMult() {
        return mult;
    }

}
