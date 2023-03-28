package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;

import java.util.Set;

public class VariableParameter extends Variable {

    private final String dataType;
    private final String name;

    public VariableParameter(String dataType, String name) {
        this.dataType = dataType;
        this.name = name;
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
        return context.getParameters().get(name).getValueBoolean(context);
    }

    @Override
    public int getValueInteger(ContextScript context) {
        return context.getParameters().get(name).getValueInteger(context);
    }

    @Override
    public float getValueFloat(ContextScript context) {
        return context.getParameters().get(name).getValueFloat(context);
    }

    @Override
    public String getValueString(ContextScript context) {
        return context.getParameters().get(name).getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        return context.getParameters().get(name).getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(ContextScript context) {
        return context.getParameters().get(name).getValueInventory(context);
    }
}
