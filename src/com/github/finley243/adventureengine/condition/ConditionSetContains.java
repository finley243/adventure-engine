package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.variable.Variable;

public class ConditionSetContains extends Condition {

    private final Variable variableSet;
    private final Variable variableString;

    public ConditionSetContains(boolean invert, Variable variableSet, Variable variableString) {
        super(invert);
        if (variableSet == null || variableString == null) throw new IllegalArgumentException("One or more variables is null");
        if (variableSet.getDataType() != Variable.DataType.STRING_SET) throw new IllegalArgumentException("Variable is not a string set");
        if (variableString.getDataType() != Variable.DataType.STRING) throw new IllegalArgumentException("Variable is not a string");
        this.variableSet = variableSet;
        this.variableString = variableString;
    }

    @Override
    protected boolean isMetInternal(Actor subject, Actor target) {
        return variableSet.getValueStringSet(subject.game()).contains(variableString.getValueString(subject.game()));
    }

}
