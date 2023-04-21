package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.List;
import java.util.Set;

public class VariableConditional extends Variable {

    private final DataType dataType;
    private final List<ConditionVariablePair> conditionVariablePairs;
    private final Variable variableElse;

    public VariableConditional(String dataType, List<ConditionVariablePair> conditionVariablePairs, Variable variableElse) {
        this.dataType = dataTypeFromString(dataType);
        this.conditionVariablePairs = conditionVariablePairs;
        this.variableElse = variableElse;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueBoolean(context);
            }
        }
        return variableElse.getValueBoolean(context);
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueInteger(context);
            }
        }
        return variableElse.getValueInteger(context);
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueFloat(context);
            }
        }
        return variableElse.getValueFloat(context);
    }

    @Override
    public String getValueString(ContextScript context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueString(context);
            }
        }
        return variableElse.getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueStringSet(context);
            }
        }
        return variableElse.getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(ContextScript context) {
        if (getDataType() != DataType.INVENTORY) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueInventory(context);
            }
        }
        return variableElse.getValueInventory(context);
    }

    @Override
    public Noun getValueNoun(ContextScript context) {
        if (getDataType() != DataType.NOUN) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.variable.getValueNoun(context);
            }
        }
        return variableElse.getValueNoun(context);
    }

    public static class ConditionVariablePair {
        private final Condition condition;
        private final Variable variable;

        public ConditionVariablePair(Condition condition, Variable variable) {
            this.condition = condition;
            this.variable = variable;
        }
    }

}
