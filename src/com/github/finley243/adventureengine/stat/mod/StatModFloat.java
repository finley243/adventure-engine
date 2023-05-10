package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModFloat extends StatMod {

    private final float mod;

    public StatModFloat(Condition condition, float mod) {
        super(condition);
        this.mod = mod;
    }

    public float getMod() {
        return mod;
    }

}
