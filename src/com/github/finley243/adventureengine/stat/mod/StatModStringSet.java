package com.github.finley243.adventureengine.stat.mod;

import com.github.finley243.adventureengine.condition.Condition;

import java.util.Set;

public class StatModStringSet extends StatMod {

    private final Set<String> additions;
    private final Set<String> cancellations;

    public StatModStringSet(Condition condition, Set<String> additions, Set<String> cancellations) {
        super(condition);
        this.additions = additions;
        this.cancellations = cancellations;
    }

    public Set<String> getAdditions() {
        return additions;
    }

    public Set<String> getCancellations() {
        return cancellations;
    }

}
