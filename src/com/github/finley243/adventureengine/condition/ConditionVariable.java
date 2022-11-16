package com.github.finley243.adventureengine.condition;

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
    public boolean isMetInternal(Actor subject, Actor target) {
        int varValue = subject.game().data().getGlobalInteger(variableID);
        return Condition.equalityCheckInt(varValue, value, equality);
    }

}
