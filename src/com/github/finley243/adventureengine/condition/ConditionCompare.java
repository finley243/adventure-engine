package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.variable.Variable;

public class ConditionCompare extends Condition {

    private final Variable variable1;
    private final Variable variable2;
    private final Equality equality;

    public ConditionCompare(boolean invert, Variable variable1, Variable variable2, Equality equality) {
        super(invert);
        if (variable1 == null || variable2 == null) {
            throw new IllegalArgumentException("One or more variables is null");
        }
        if (!variable1.canCompareTo(variable2)) {
            throw new IllegalArgumentException("Variables cannot be compared");
        }
        this.variable1 = variable1;
        this.variable2 = variable2;
        this.equality = equality;
    }

    @Override
    protected boolean isMetInternal(Actor subject, Actor target) {
        if ((variable1.getDataType() == Variable.DataType.INTEGER || variable1.getDataType() == Variable.DataType.FLOAT) &&
            (variable2.getDataType() == Variable.DataType.INTEGER || variable2.getDataType() == Variable.DataType.FLOAT)) {
            float value1;
            float value2;
            if (variable1.getDataType() == Variable.DataType.INTEGER) {
                value1 = variable1.getValueInteger(subject.game());
            } else {
                value1 = variable1.getValueFloat(subject.game());
            }
            if (variable2.getDataType() == Variable.DataType.INTEGER) {
                value2 = variable2.getValueInteger(subject.game());
            } else {
                value2 = variable2.getValueFloat(subject.game());
            }
            return Condition.equalityCheckFloat(value1, value2, equality);
        } else if (variable1.getDataType() == Variable.DataType.BOOLEAN) {
            return variable1.getValueBoolean(subject.game()) == variable2.getValueBoolean(subject.game());
        } else if (variable1.getDataType() == Variable.DataType.STRING) {
            return variable1.getValueString(subject.game()).equals(variable2.getValueString(subject.game()));
        } else if (variable1.getDataType() == Variable.DataType.STRING_SET) {
            return variable1.getValueStringSet(subject.game()).equals(variable2.getValueStringSet(subject.game()));
        }
        return false;
    }

}
