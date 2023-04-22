package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionConstant extends Expression {

    private final DataType dataType;
    private final Boolean valueBoolean;
    private final Integer valueInteger;
    private final Float valueFloat;
    private final String valueString;
    private final Set<String> valueStringSet;
    private final Inventory valueInventory;
    private final Noun valueNoun;

    private ExpressionConstant(DataType dataType, Boolean valueBoolean, Integer valueInteger, Float valueFloat, String valueString, Set<String> valueStringSet, Inventory valueInventory, Noun valueNoun) {
        this.dataType = dataType;
        this.valueBoolean = valueBoolean;
        this.valueInteger = valueInteger;
        this.valueFloat = valueFloat;
        this.valueString = valueString;
        this.valueStringSet = valueStringSet;
        this.valueInventory = valueInventory;
        this.valueNoun = valueNoun;
    }

    public ExpressionConstant(boolean value) {
        this(DataType.BOOLEAN, value, null, null, null, null, null, null);
    }

    public ExpressionConstant(int value) {
        this(DataType.INTEGER, null, value, null, null, null, null, null);
    }

    public ExpressionConstant(float value) {
        this(DataType.FLOAT, null, null, value, null, null, null, null);
    }

    public ExpressionConstant(String value) {
        this(DataType.STRING, null, null, null, value, null, null, null);
    }

    public ExpressionConstant(Set<String> value) {
        this(DataType.STRING_SET, null, null, null, null, value, null, null);
    }

    public ExpressionConstant(Inventory value) {
        this(DataType.INVENTORY, null, null, null, null, null, value, null);
    }

    public ExpressionConstant(Noun value) {
        this(DataType.NOUN, null, null, null, null, null, null, value);
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

}
