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
    private final Expression stat;

    public ExpressionStat(StatHolderReference holder, String dataType, Expression stat) {
        if (stat != null && stat.getDataType() != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
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
        if (stat == null) return false;
        String statName = stat.getValueString(context);
        return holder.getHolder(context).getStatValue(statName, context).getValueBoolean(context);
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        if (stat == null) return 0;
        String statName = stat.getValueString(context);
        return holder.getHolder(context).getStatValue(statName, context).getValueInteger(context);
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        if (stat == null) return 0.0f;
        String statName = stat.getValueString(context);
        return holder.getHolder(context).getStatValue(statName, context).getValueFloat(context);
    }

    @Override
    public String getValueString(Context context) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        if (stat == null) return null;
        String statName = stat.getValueString(context);
        return holder.getHolder(context).getStatValue(statName, context).getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        if (stat == null) return null;
        String statName = stat.getValueString(context);
        return holder.getHolder(context).getStatValue(statName, context).getValueStringSet(context);
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
