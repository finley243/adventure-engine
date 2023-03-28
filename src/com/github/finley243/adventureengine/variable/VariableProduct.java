package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;

import java.util.List;
import java.util.Set;

public class VariableProduct extends Variable {

    private final List<Variable> variables;
    private final boolean isFloat;

    public VariableProduct(List<Variable> variables) {
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
        int product = 1;
        for (Variable variable : variables) {
            product *= variable.getValueInteger(context);
        }
        return product;
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        float product = 1.0f;
        for (Variable variable : variables) {
            if (variable.getDataType() == DataType.FLOAT) {
                product *= variable.getValueFloat(context);
            } else {
                product *= variable.getValueInteger(context);
            }
        }
        return product;
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

}
