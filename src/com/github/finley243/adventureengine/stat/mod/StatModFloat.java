package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModFloat extends StatMod {

    private final float mod;
    private final float mult;

    public StatModFloat(Condition condition, float mod, float mult) {
        super(condition);
        this.mod = mod;
        this.mult = mult;
    }

    public float getMod() {
        return mod;
    }

    public float getMult() {
        return mult;
    }

}
