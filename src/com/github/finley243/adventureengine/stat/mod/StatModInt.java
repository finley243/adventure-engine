package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModInt extends StatMod {

    private final int mod;
    private final float mult;

    public StatModInt(Condition condition, int mod, float mult) {
        super(condition);
        this.mod = mod;
        this.mult = mult;
    }

    public int getMod() {
        return mod;
    }

    public float getMult() {
        return mult;
    }

}
