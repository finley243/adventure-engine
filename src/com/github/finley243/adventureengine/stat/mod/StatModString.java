package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

public class StatModString extends StatMod {

    private final String value;
    private final int priority;

    public StatModString(Condition condition, String value, int priority) {
        super(condition);
        this.value = value;
        this.priority = priority;
    }

    public String getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

}
