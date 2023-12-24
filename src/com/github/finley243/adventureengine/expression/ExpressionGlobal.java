package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

import java.util.Set;

public class ExpressionGlobal extends Expression {

    private final DataType dataType;
    private final Expression expressionID;

    public ExpressionGlobal(String dataType, Expression expressionID) {
        this.dataType = dataTypeFromString(dataType);
        this.expressionID = expressionID;
    }

    @Override
    public DataType getDataType(Context context) {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (getDataType(context) != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return context.game().data().getGlobalBoolean(expressionID.getValueString(context));
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType(context) != DataType.INTEGER) throw new UnsupportedOperationException();
        return context.game().data().getGlobalInteger(expressionID.getValueString(context));
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        return context.game().data().getGlobalFloat(expressionID.getValueString(context));
    }

    @Override
    public String getValueString(Context context) {
        if (getDataType(context) != DataType.STRING) throw new UnsupportedOperationException();
        return context.game().data().getGlobalString(expressionID.getValueString(context));
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (getDataType(context) != DataType.STRING_SET) throw new UnsupportedOperationException();
        return context.game().data().getGlobalStringSet(expressionID.getValueString(context));
    }

}
