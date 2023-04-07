package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class VariableLiteral extends Variable {

    private final DataType dataType;
    private final Boolean valueBoolean;
    private final Integer valueInteger;
    private final Float valueFloat;
    private final String valueString;
    private final Set<String> valueStringSet;
    private final Inventory valueInventory;
    private final Noun valueNoun;

    private VariableLiteral(DataType dataType, Boolean valueBoolean, Integer valueInteger, Float valueFloat, String valueString, Set<String> valueStringSet, Inventory valueInventory, Noun valueNoun) {
        this.dataType = dataType;
        this.valueBoolean = valueBoolean;
        this.valueInteger = valueInteger;
        this.valueFloat = valueFloat;
        this.valueString = valueString;
        this.valueStringSet = valueStringSet;
        this.valueInventory = valueInventory;
        this.valueNoun = valueNoun;
    }

    public VariableLiteral(String dataType, boolean value) {
        this(dataTypeFromString(dataType), value, null, null, null, null, null, null);
    }

    public VariableLiteral(String dataType, int value) {
        this(dataTypeFromString(dataType), null, value, null, null, null, null, null);
    }

    public VariableLiteral(String dataType, float value) {
        this(dataTypeFromString(dataType), null, null, value, null, null, null, null);
    }

    public VariableLiteral(String dataType, String value) {
        this(dataTypeFromString(dataType), null, null, null, value, null, null, null);
    }

    public VariableLiteral(String dataType, Set<String> value) {
        this(dataTypeFromString(dataType), null, null, null, null, value, null, null);
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

    @Override
    public Inventory getValueInventory(ContextScript context) {
        if (getDataType() != DataType.INVENTORY) throw new UnsupportedOperationException();
        if (valueInventory == null) throw new UnsupportedOperationException();
        return valueInventory;
    }

    @Override
    public Noun getValueNoun(ContextScript context) {
        if (getDataType() != DataType.NOUN) throw new UnsupportedOperationException();
        if (valueNoun == null) throw new UnsupportedOperationException();
        return valueNoun;
    }

    public static VariableLiteral convertToLiteral(Variable variable, ContextScript context) {
        return switch (variable.getDataType()) {
            case BOOLEAN -> new VariableLiteral(DataType.BOOLEAN, variable.getValueBoolean(context), null, null, null, null, null, null);
            case INTEGER -> new VariableLiteral(DataType.INTEGER, null, variable.getValueInteger(context), null, null, null, null, null);
            case FLOAT -> new VariableLiteral(DataType.FLOAT, null, null, variable.getValueFloat(context), null, null, null, null);
            case STRING -> new VariableLiteral(DataType.STRING, null, null, null, variable.getValueString(context), null, null, null);
            case STRING_SET -> new VariableLiteral(DataType.STRING_SET, null, null, null, null, variable.getValueStringSet(context), null, null);
            case INVENTORY -> new VariableLiteral(DataType.INVENTORY, null, null, null, null, null, variable.getValueInventory(context), null);
            case NOUN -> new VariableLiteral(DataType.NOUN, null, null, null, null, null, null, variable.getValueNoun(context));
            default -> null;
        };
    }

}
