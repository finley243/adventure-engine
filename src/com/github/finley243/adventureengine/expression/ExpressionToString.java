package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;

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
    public String getValueString(ContextScript context) {
        return switch (expression.getDataType()) {
            case BOOLEAN -> Boolean.toString(expression.getValueBoolean(context));
            case INTEGER -> Integer.toString(expression.getValueInteger(context));
            case FLOAT -> Float.toString(expression.getValueFloat(context));
            case STRING -> expression.getValueString(context);
            case STRING_SET -> expression.getValueStringSet(context).toString();
            case INVENTORY -> throw new UnsupportedOperationException("Cannot convert inventory to string");
            case NOUN -> expression.getValueNoun(context).getName();
        };
    }

}
