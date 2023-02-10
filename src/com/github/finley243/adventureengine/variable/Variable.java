package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;

import java.util.Set;

public abstract class Variable {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET
    }

    public abstract DataType getDataType();

    public abstract boolean getValueBoolean(ContextScript context);

    public abstract int getValueInteger(ContextScript context);

    public abstract float getValueFloat(ContextScript context);

    public abstract String getValueString(ContextScript context);

    public abstract Set<String> getValueStringSet(ContextScript context);

    public boolean canCompareTo(Variable other) {
        if (this.getDataType() == DataType.STRING_SET || other.getDataType() == DataType.STRING_SET) {
            return false;
        }
        if (this.getDataType() == DataType.INTEGER || this.getDataType() == DataType.FLOAT) {
            return other.getDataType() == DataType.INTEGER || other.getDataType() == DataType.FLOAT;
        }
        return this.getDataType() == other.getDataType();
    }

}
