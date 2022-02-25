package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ConditionVariable extends Condition {

    private final String variableID;
    private final Equality equality;
    private final int value;

    public ConditionVariable(boolean invert, String variableID, Equality equality, int value) {
        super(invert);
        this.variableID = variableID;
        this.equality = equality;
        this.value = value;
    }

    @Override
    public boolean isMet(Actor subject) {
        int varValue = Data.getVariable(variableID);
        return Condition.equalityCheckInt(varValue, value, equality, invert);
    }

}
