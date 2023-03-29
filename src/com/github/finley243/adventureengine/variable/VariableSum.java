package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.List;
import java.util.Set;

public class VariableSum extends Variable {

    private final List<Variable> variables;
    private final boolean isFloat;

    public VariableSum(List<Variable> variables) {
        if (variables.isEmpty()) throw new IllegalArgumentException("Variable list is empty");
        this.variables = variables;
        boolean hasFloatVariable = false;
        for (Variable variable : variables) {
            if (variable.getDataType() == DataType.FLOAT) {
                hasFloatVariable = true;
            } else if (variable.getDataType() != DataType.INTEGER) {
                throw new IllegalArgumentException("Only integers and floats are allowed");
            }
        }
        this.isFloat = hasFloatVariable;
    }

    @Override
    public DataType getDataType() {
        if (isFloat) {
            return DataType.FLOAT;
        } else {
            return DataType.INTEGER;
        }
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        int sum = 0;
        for (Variable variable : variables) {
            sum += variable.getValueInteger(context);
        }
        return sum;
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        float sum = 0.0f;
        for (Variable variable : variables) {
            if (variable.getDataType() == DataType.FLOAT) {
                sum += variable.getValueFloat(context);
            } else {
                sum += variable.getValueInteger(context);
            }
        }
        return sum;
    }

    @Override
    public String getValueString(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Inventory getValueInventory(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Noun getValueNoun(ContextScript context) {
        throw new UnsupportedOperationException();
    }

}
