package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionGlobal extends Expression {

    private final Expression expressionID;

    public ExpressionGlobal(Expression expressionID) {
        this.expressionID = expressionID;
    }

    @Override
    public DataType getDataType(Context context) {
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getDataType(context);
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (getDataType(context) != DataType.BOOLEAN) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueBoolean(context);
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType(context) != DataType.INTEGER) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueInteger(context);
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueFloat(context);
    }

    @Override
    public String getValueString(Context context) {
        if (getDataType(context) != DataType.STRING) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (getDataType(context) != DataType.STRING_SET) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(Context context) {
        if (getDataType(context) != DataType.INVENTORY) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueInventory(context);
    }

    @Override
    public Noun getValueNoun(Context context) {
        if (getDataType(context) != DataType.NOUN) throw new UnsupportedOperationException();
        if (expressionID.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionGlobal expressionID is not a string");
        return context.game().data().getGlobalExpression(expressionID.getValueString(context)).getValueNoun(context);
    }

}
