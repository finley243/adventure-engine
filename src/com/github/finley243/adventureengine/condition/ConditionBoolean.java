package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.variable.Variable;

public class ConditionBoolean extends Condition {

    private final Variable variable;

    public ConditionBoolean(boolean invert, Variable variable) {
        super(invert);
        if (variable == null) throw new IllegalArgumentException("Variable is null");
        if (variable.getDataType() != Variable.DataType.BOOLEAN) throw new IllegalArgumentException("Variable is non-boolean");
        this.variable = variable;
    }

    @Override
    protected boolean isMetInternal(Actor subject, Actor target) {
        return variable.getValueBoolean(subject.game(), subject, target);
    }

}
