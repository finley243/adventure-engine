package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ConditionVariable implements Condition {

    private final String variableID;
    private final Equality equality;
    private final int value;

    public ConditionVariable(String variableID, Equality equality, int value) {
        this.variableID = variableID;
        this.equality = equality;
        this.value = value;
    }

    @Override
    public boolean isMet(Actor subject) {
        int varValue = Data.getVariable(variableID);
        switch(equality) {
            case LESS:
                return varValue < value;
            case GREATER:
                return varValue > value;
            case LESS_EQUAL:
                return varValue <= value;
            case GREATER_EQUAL:
                return varValue >= value;
            case EQUAL:
                return varValue == value;
            case NOT_EQUAL:
                return varValue != value;
            default:
                return false;
        }
    }

    @Override
    public String getChoiceTag() {
        return null;
    }

}
