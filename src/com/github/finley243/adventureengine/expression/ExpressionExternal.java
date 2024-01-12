package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionExternal extends Expression {

    private final String externalID;

    public ExpressionExternal(String externalID) {
        this.externalID = externalID;
    }

    @Override
    public DataType getDataType(Context context) {
        return context.game().data().getExpression(externalID).getDataType(context);
    }

    @Override
    public boolean getValueBoolean(Context context) {
        return context.game().data().getExpression(externalID).getValueBoolean(context);
    }

    @Override
    public int getValueInteger(Context context) {
        return context.game().data().getExpression(externalID).getValueInteger(context);
    }

    @Override
    public float getValueFloat(Context context) {
        return context.game().data().getExpression(externalID).getValueFloat(context);
    }

    @Override
    public String getValueString(Context context) {
        return context.game().data().getExpression(externalID).getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        return context.game().data().getExpression(externalID).getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(Context context) {
        return context.game().data().getExpression(externalID).getValueInventory(context);
    }

    @Override
    public Noun getValueNoun(Context context) {
        return context.game().data().getExpression(externalID).getValueNoun(context);
    }

}
