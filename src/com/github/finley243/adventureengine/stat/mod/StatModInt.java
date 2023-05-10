package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModInt extends StatMod {

    private final int mod;

    public StatModInt(Condition condition, int mod) {
        super(condition);
        this.mod = mod;
    }

    public int getMod() {
        return mod;
    }

}
