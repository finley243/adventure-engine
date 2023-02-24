package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.Set;

public class VariableStat extends Variable {

    private final StatHolderReference holder;
    private final String dataType;
    private final String stat;

    public VariableStat(StatHolderReference holder, String dataType, String stat) {
        this.holder = holder;
        this.dataType = dataType;
        this.stat = stat;
    }

    @Override
    public DataType getDataType() {
        switch (dataType) {
            case "boolean":
                return DataType.BOOLEAN;
            case "int":
                return DataType.INTEGER;
            case "float":
                return DataType.FLOAT;
            case "string":
                return DataType.STRING;
            case "stringSet":
                return DataType.STRING_SET;
            case "inventory":
                return DataType.INVENTORY;
            default:
                return null;
        }
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueBoolean(stat);
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueInt(stat);
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueFloat(stat);
    }

    @Override
    public String getValueString(ContextScript context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueString(stat);
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueStringSet(stat);
    }

    @Override
    public Inventory getValueInventory(ContextScript context) {
        if (getDataType() != DataType.INVENTORY) throw new UnsupportedOperationException();
        return holder.getHolder(context).getInventory();
    }

}
