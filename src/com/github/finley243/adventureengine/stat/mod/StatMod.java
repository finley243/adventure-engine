package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public abstract class StatMod {

    private final Condition condition;

    public StatMod(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

}
