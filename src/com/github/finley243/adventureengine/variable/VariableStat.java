package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.Game;
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
            default:
                return null;
        }
    }

    @Override
    public boolean getValueBoolean(Game game) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return holder.getHolder(game).getStatValueBoolean(stat);
    }

    @Override
    public int getValueInteger(Game game) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return holder.getHolder(game).getStatValueInt(stat);
    }

    @Override
    public float getValueFloat(Game game) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return holder.getHolder(game).getStatValueFloat(stat);
    }

    @Override
    public String getValueString(Game game) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return holder.getHolder(game).getStatValueString(stat);
    }

    @Override
    public Set<String> getValueStringSet(Game game) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return holder.getHolder(game).getStatValueStringSet(stat);
    }

}
