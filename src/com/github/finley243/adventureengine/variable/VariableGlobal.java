package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class VariableGlobal extends Variable {

    private final String dataType;
    private final String variableID;

    public VariableGlobal(String dataType, String variableID) {
        this.dataType = dataType;
        this.variableID = variableID;
    }

    @Override
    public DataType getDataType() {
        switch (dataType) {
            case "boolean":
                return DataType.BOOLEAN;
            case "int":
                return DataType.INTEGER;
            case "float":
                return DataType.FLOAT;
            case "string":
                return DataType.STRING;
            case "stringSet":
                return DataType.STRING_SET;
            default:
                return null;
        }
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return context.game().data().getGlobalBoolean(variableID);
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return context.game().data().getGlobalInteger(variableID);
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return context.game().data().getGlobalFloat(variableID);
    }

    @Override
    public String getValueString(ContextScript context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return context.game().data().getGlobalString(variableID);
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return context.game().data().getGlobalStringSet(variableID);
    }

    @Override
    public Inventory getValueInventory(ContextScript context) {
        throw new UnsupportedOperationException("Inventory can not be referenced as a global variable");
    }

    @Override
    public Noun getValueNoun(ContextScript context) {
        throw new UnsupportedOperationException("Noun can not be referenced as a global variable");
    }

}
