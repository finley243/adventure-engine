package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public class ExpressionStat extends Expression {

    private final StatHolderReference holder;
    private final Expression stat;

    public ExpressionStat(StatHolderReference holder, Expression stat) {
        if (holder == null) throw new IllegalArgumentException("ExpressionStat holder parameter is null");
        if (stat == null) throw new IllegalArgumentException("ExpressionStat stat parameter is null");
        this.holder = holder;
        this.stat = stat;
    }

    @Override
    public DataType getDataType(Context context) {
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        return holder.getHolder(context).getStatValue(stat.getValueString(context), context).getDataType(context);
    }

    @Override
    public boolean getValueBoolean(Context context) {
        if (getDataType(context) != DataType.BOOLEAN) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return false;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " - mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return false;
        }
        return valueExpression.getValueBoolean(context);
    }

    @Override
    public int getValueInteger(Context context) {
        if (getDataType(context) != DataType.INTEGER) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return 0;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " has mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return 0;
        }
        return valueExpression.getValueInteger(context);
    }

    @Override
    public float getValueFloat(Context context) {
        if (getDataType(context) != DataType.FLOAT) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return 0;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " has mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return 0;
        }
        return valueExpression.getValueFloat(context);
    }

    @Override
    public String getValueString(Context context) {
        if (getDataType(context) != DataType.STRING) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return null;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " has mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return null;
        }
        return valueExpression.getValueString(context);
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (getDataType(context) != DataType.STRING_SET) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return null;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " has mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return null;
        }
        return valueExpression.getValueStringSet(context);
    }

    @Override
    public Inventory getValueInventory(Context context) {
        if (getDataType(context) != DataType.INVENTORY) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return null;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " has mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return null;
        }
        return valueExpression.getValueInventory(context);
    }

    @Override
    public Noun getValueNoun(Context context) {
        if (getDataType(context) != DataType.NOUN) throw new UnsupportedOperationException();
        if (stat.getDataType(context) != DataType.STRING) throw new IllegalArgumentException("ExpressionStat stat parameter is not a string");
        String statName = stat.getValueString(context);
        Expression valueExpression = holder.getHolder(context).getStatValue(statName, context);
        if (valueExpression == null) {
            context.game().log().print("ExpressionStat - stat " + statName + " does not exist on holder " + holder.getHolder(context));
            return null;
        }
        if (valueExpression.getDataType(context) != getDataType(context)) {
            context.game().log().print("ExpressionStat " + statName + " has mismatched data type (stat type: " + valueExpression.getDataType(context) + ", expression type: " + getDataType(context) + ")");
            return null;
        }
        return valueExpression.getValueNoun(context);
    }

}
