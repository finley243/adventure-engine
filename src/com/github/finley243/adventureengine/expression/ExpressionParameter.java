package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionParameter extends Expression {

    private final DataType dataType;
    private final String name;

    public ExpressionParameter(String dataType, String name) {
        this.dataType = dataTypeFromString(dataType);
        this.name = name;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return context.getParameters().get(name).getExpression().getValueBoolean(context);
    }

    @Override
    public int getValueInteger(Context context) {
        return context.getParameters().get(name).getExpression().getValueInteger(context);
    }

    @Override
    public float getValueFloat(Context context) {
        return context.getParameters().get(name).getExpression().getValueFloat(context);
    }

    @Override
    public String getValueString(Context context) {
        return context.getParameters().get(name).getExpression().getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        return context.getParameters().get(name).getExpression().getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(Context context) {
        return context.getParameters().get(name).getExpression().getValueInventory(context);
    }

    @Override
    public Noun getValueNoun(Context context) {
        return context.getParameters().get(name).getExpression().getValueNoun(context);
    }

}
