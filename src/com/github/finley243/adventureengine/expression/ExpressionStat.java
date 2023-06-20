package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionStat extends Expression {

    private final StatHolderReference holder;
    private final DataType dataType;
    private final String stat;

    public ExpressionStat(StatHolderReference holder, String dataType, String stat) {
        this.holder = holder;
        this.dataType = dataTypeFromString(dataType);
        this.stat = stat;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueBoolean(stat, context);
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueInt(stat, context);
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueFloat(stat, context);
    }

    @Override
    public String getValueString(Context context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueString(stat, context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return holder.getHolder(context).getValueStringSet(stat, context);
    }

    @Override
    public Inventory getValueInventory(Context context) {
        if (getDataType() != DataType.INVENTORY) throw new UnsupportedOperationException();
        return holder.getHolder(context).getInventory();
    }

    @Override
    public Noun getValueNoun(Context context) {
        if (getDataType() != DataType.NOUN) throw new UnsupportedOperationException();
        StatHolder statHolderObject = holder.getHolder(context);
        if (statHolderObject instanceof Noun) {
            return (Noun) statHolderObject;
        } else {
            throw new IllegalArgumentException("Specified StatHolder is not a Noun");
        }
    }

}
