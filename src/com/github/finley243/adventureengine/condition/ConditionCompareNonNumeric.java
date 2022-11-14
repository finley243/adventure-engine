package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.variable.Variable;

public class ConditionCompareNonNumeric extends Condition {

    private final Variable variable1;
    private final Variable variable2;

    public ConditionCompareNonNumeric(boolean invert, Variable variable1, Variable variable2) {
        super(invert);
        if (variable1.canCompareTo(variable2)) throw new IllegalArgumentException("Variables cannot be compared");
        if (!(variable1.getDataType() == Variable.DataType.INTEGER || variable1.getDataType() == Variable.DataType.FLOAT) ||
            !(variable2.getDataType() == Variable.DataType.INTEGER || variable2.getDataType() == Variable.DataType.FLOAT)) {
            throw new IllegalArgumentException("One or more variables is numeric");
        }
        this.variable1 = variable1;
        this.variable2 = variable2;
    }

    @Override
    protected boolean isMetInternal(Actor subject, Actor target) {
        if (variable1.getDataType() == Variable.DataType.BOOLEAN) {
            return variable1.getValueBoolean(subject.game()) == variable2.getValueBoolean(subject.game());
        } else if (variable1.getDataType() == Variable.DataType.STRING) {
            return variable1.getValueString(subject.game()).equals(variable2.getValueString(subject.game()));
        } else if (variable1.getDataType() == Variable.DataType.STRING_SET) {
            return variable1.getValueStringSet(subject.game()).equals(variable2.getValueStringSet(subject.game()));
        }
        return false;
    }

}
