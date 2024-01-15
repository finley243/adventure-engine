package com.github.finley243.adventureengine.expression;

public class ExpressionToString extends Expression {

    private final Expression expression;

    public ExpressionToString(Expression expression) {
        this.expression = expression;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public String getValueString() {
        return switch (expression.getDataType()) {
            case BOOLEAN -> Boolean.toString(expression.getValueBoolean());
            case INTEGER -> Integer.toString(expression.getValueInteger());
            case FLOAT -> Float.toString(expression.getValueFloat());
            case STRING -> expression.getValueString();
            case STRING_SET -> expression.getValueStringSet().toString();
            case INVENTORY -> throw new UnsupportedOperationException("Cannot convert inventory to string");
            case NOUN -> expression.getValueNoun().getName();
        };
    }

}
