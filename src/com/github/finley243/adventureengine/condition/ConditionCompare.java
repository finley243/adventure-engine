package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;
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
    protected boolean isMetInternal(ContextScript context) {
        if ((variable1.getDataType() == Variable.DataType.INTEGER || variable1.getDataType() == Variable.DataType.FLOAT) &&
            (variable2.getDataType() == Variable.DataType.INTEGER || variable2.getDataType() == Variable.DataType.FLOAT)) {
            float value1;
            float value2;
            if (variable1.getDataType() == Variable.DataType.INTEGER) {
                value1 = variable1.getValueInteger(context);
            } else {
                value1 = variable1.getValueFloat(context);
            }
            if (variable2.getDataType() == Variable.DataType.INTEGER) {
                value2 = variable2.getValueInteger(context);
            } else {
                value2 = variable2.getValueFloat(context);
            }
            return Condition.equalityCheckFloat(value1, value2, equality);
        } else if (variable1.getDataType() == Variable.DataType.BOOLEAN) {
            return variable1.getValueBoolean(context) == variable2.getValueBoolean(context);
        } else if (variable1.getDataType() == Variable.DataType.STRING) {
            return variable1.getValueString(context).equals(variable2.getValueString(context));
        } else if (variable1.getDataType() == Variable.DataType.STRING_SET) {
            return variable1.getValueStringSet(context).equals(variable2.getValueStringSet(context));
        }
        return false;
    }

}
