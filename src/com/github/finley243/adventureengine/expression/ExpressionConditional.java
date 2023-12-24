package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.List;
import java.util.Set;

public class ExpressionConditional extends Expression {

    private final DataType dataType;
    private final List<ConditionVariablePair> conditionVariablePairs;
    private final Expression expressionElse;

    public ExpressionConditional(String dataType, List<ConditionVariablePair> conditionVariablePairs, Expression expressionElse) {
        this.dataType = dataTypeFromString(dataType);
        this.conditionVariablePairs = conditionVariablePairs;
        this.expressionElse = expressionElse;
    }

    @Override
    public DataType getDataType(Context context) {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (getDataType(context) != DataType.BOOLEAN) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueBoolean(context);
            }
        }
        return expressionElse.getValueBoolean(context);
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType(context) != DataType.INTEGER) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueInteger(context);
            }
        }
        return expressionElse.getValueInteger(context);
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueFloat(context);
            }
        }
        return expressionElse.getValueFloat(context);
    }

    @Override
    public String getValueString(Context context) {
        if (getDataType(context) != DataType.STRING) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueString(context);
            }
        }
        return expressionElse.getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (getDataType(context) != DataType.STRING_SET) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueStringSet(context);
            }
        }
        return expressionElse.getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(Context context) {
        if (getDataType(context) != DataType.INVENTORY) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueInventory(context);
            }
        }
        return expressionElse.getValueInventory(context);
    }

    @Override
    public Noun getValueNoun(Context context) {
        if (getDataType(context) != DataType.NOUN) throw new UnsupportedOperationException();
        for (ConditionVariablePair pair : conditionVariablePairs) {
            if (pair.condition.isMet(context)) {
                return pair.expression.getValueNoun(context);
            }
        }
        return expressionElse.getValueNoun(context);
    }

    public static class ConditionVariablePair {
        private final Condition condition;
        private final Expression expression;

        public ConditionVariablePair(Condition condition, Expression expression) {
            this.condition = condition;
            this.expression = expression;
        }
    }

}
