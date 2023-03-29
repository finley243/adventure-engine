package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;

import java.util.Set;

public class VariableLiteral extends Variable {

    private final DataType dataType;
    private final Boolean valueBoolean;
    private final Integer valueInteger;
    private final Float valueFloat;
    private final String valueString;
    private final Set<String> valueStringSet;

    public VariableLiteral(String dataType, boolean value) {
        this.dataType = dataTypeFromString(dataType);
        this.valueBoolean = value;
        this.valueInteger = null;
        this.valueFloat = null;
        this.valueString = null;
        this.valueStringSet = null;
    }

    public VariableLiteral(String dataType, int value) {
        this.dataType = dataTypeFromString(dataType);
        this.valueBoolean = null;
        this.valueInteger = value;
        this.valueFloat = null;
        this.valueString = null;
        this.valueStringSet = null;
    }

    public VariableLiteral(String dataType, float value) {
        this.dataType = dataTypeFromString(dataType);
        this.valueBoolean = null;
        this.valueInteger = null;
        this.valueFloat = value;
        this.valueString = null;
        this.valueStringSet = null;
    }

    public VariableLiteral(String dataType, String value) {
        this.dataType = dataTypeFromString(dataType);
        this.valueBoolean = null;
        this.valueInteger = null;
        this.valueFloat = null;
        this.valueString = value;
        this.valueStringSet = null;
    }

    public VariableLiteral(String dataType, Set<String> value) {
        this.dataType = dataTypeFromString(dataType);
        this.valueBoolean = null;
        this.valueInteger = null;
        this.valueFloat = null;
        this.valueString = null;
        this.valueStringSet = value;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        if (valueBoolean == null) throw new UnsupportedOperationException();
        return valueBoolean;
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        if (valueInteger == null) throw new UnsupportedOperationException();
        return valueInteger;
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        if (valueFloat == null) throw new UnsupportedOperationException();
        return valueFloat;
    }

    @Override
    public String getValueString(ContextScript context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        if (valueString == null) throw new UnsupportedOperationException();
        return valueString;
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        if (valueStringSet == null) throw new UnsupportedOperationException();
        return valueStringSet;
    }

}
