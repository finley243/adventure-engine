package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;

import java.util.Set;

public class ExpressionGlobal extends Expression {

    private final DataType dataType;
    private final Expression expressionID;

    public ExpressionGlobal(String dataType, Expression expressionID) {
        this.dataType = dataTypeFromString(dataType);
        this.expressionID = expressionID;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return context.game().data().getGlobalBoolean(expressionID.getValueString(context));
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return context.game().data().getGlobalInteger(expressionID.getValueString(context));
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return context.game().data().getGlobalFloat(expressionID.getValueString(context));
    }

    @Override
    public String getValueString(ContextScript context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return context.game().data().getGlobalString(expressionID.getValueString(context));
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return context.game().data().getGlobalStringSet(expressionID.getValueString(context));
    }

}
