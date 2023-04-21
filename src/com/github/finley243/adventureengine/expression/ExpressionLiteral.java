package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionLiteral extends Expression {

    private final DataType dataType;
    private final Boolean valueBoolean;
    private final Integer valueInteger;
    private final Float valueFloat;
    private final String valueString;
    private final Set<String> valueStringSet;
    private final Inventory valueInventory;
    private final Noun valueNoun;

    private ExpressionLiteral(DataType dataType, Boolean valueBoolean, Integer valueInteger, Float valueFloat, String valueString, Set<String> valueStringSet, Inventory valueInventory, Noun valueNoun) {
        this.dataType = dataType;
        this.valueBoolean = valueBoolean;
        this.valueInteger = valueInteger;
        this.valueFloat = valueFloat;
        this.valueString = valueString;
        this.valueStringSet = valueStringSet;
        this.valueInventory = valueInventory;
        this.valueNoun = valueNoun;
    }

    public ExpressionLiteral(boolean value) {
        this(DataType.BOOLEAN, value, null, null, null, null, null, null);
    }

    public ExpressionLiteral(int value) {
        this(DataType.INTEGER, null, value, null, null, null, null, null);
    }

    public ExpressionLiteral(float value) {
        this(DataType.FLOAT, null, null, value, null, null, null, null);
    }

    public ExpressionLiteral(String value) {
        this(DataType.STRING, null, null, null, value, null, null, null);
    }

    public ExpressionLiteral(Set<String> value) {
        this(DataType.STRING_SET, null, null, null, null, value, null, null);
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

    public static ExpressionLiteral convertToLiteral(Expression expression, ContextScript context) {
        return switch (expression.getDataType()) {
            case BOOLEAN -> new ExpressionLiteral(DataType.BOOLEAN, expression.getValueBoolean(context), null, null, null, null, null, null);
            case INTEGER -> new ExpressionLiteral(DataType.INTEGER, null, expression.getValueInteger(context), null, null, null, null, null);
            case FLOAT -> new ExpressionLiteral(DataType.FLOAT, null, null, expression.getValueFloat(context), null, null, null, null);
            case STRING -> new ExpressionLiteral(DataType.STRING, null, null, null, expression.getValueString(context), null, null, null);
            case STRING_SET -> new ExpressionLiteral(DataType.STRING_SET, null, null, null, null, expression.getValueStringSet(context), null, null);
            case INVENTORY -> new ExpressionLiteral(DataType.INVENTORY, null, null, null, null, null, expression.getValueInventory(context), null);
            case NOUN -> new ExpressionLiteral(DataType.NOUN, null, null, null, null, null, null, expression.getValueNoun(context));
            default -> null;
        };
    }

}
