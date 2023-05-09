package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModBoolean extends StatMod {

    private final boolean value;
    private final int priority;

    public StatModBoolean(Condition condition, boolean value, int priority) {
        super(condition);
        this.value = value;
        this.priority = priority;
    }

    public boolean getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

}
