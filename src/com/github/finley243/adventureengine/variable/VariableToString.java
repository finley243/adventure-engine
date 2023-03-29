package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;

public class VariableToString extends Variable {

    private final Variable variable;

    public VariableToString(Variable variable) {
        this.variable = variable;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public String getValueString(ContextScript context) {
        return switch (variable.getDataType()) {
            case BOOLEAN -> Boolean.toString(variable.getValueBoolean(context));
            case INTEGER -> Integer.toString(variable.getValueInteger(context));
            case FLOAT -> Float.toString(variable.getValueFloat(context));
            case STRING -> variable.getValueString(context);
            case STRING_SET -> variable.getValueStringSet(context).toString();
            case INVENTORY -> throw new UnsupportedOperationException("Cannot convert inventory to string");
            case NOUN -> variable.getValueNoun(context).getName();
        };
    }

}
