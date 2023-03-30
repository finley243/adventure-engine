package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;

import java.util.Set;

public class VariableGlobal extends Variable {

    private final DataType dataType;
    private final Variable variableID;

    public VariableGlobal(String dataType, Variable variableID) {
        this.dataType = dataTypeFromString(dataType);
        this.variableID = variableID;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return context.game().data().getGlobalBoolean(variableID.getValueString(context));
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return context.game().data().getGlobalInteger(variableID.getValueString(context));
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return context.game().data().getGlobalFloat(variableID.getValueString(context));
    }

    @Override
    public String getValueString(ContextScript context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return context.game().data().getGlobalString(variableID.getValueString(context));
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return context.game().data().getGlobalStringSet(variableID.getValueString(context));
    }

}
