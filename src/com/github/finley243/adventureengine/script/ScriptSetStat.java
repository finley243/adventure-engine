package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptSetStat extends Script {

    private final StatHolderReference holder;
    private final Script statName;
    private final Script statValue;

    public ScriptSetStat(StatHolderReference holder, Script statName, Script statValue) {
        if (holder == null) throw new IllegalArgumentException("ScriptSetState stat holder is null");
        if (statName == null) throw new IllegalArgumentException("ScriptSetState state name is null");
        this.holder = holder;
        this.statName = statName;
        this.statValue = statValue;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression statNameExpression = Expression.fromScript(statName, context);
        if (statNameExpression == null) return new ScriptReturnData(null, false, false, "Specified stat name is null");
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, false, false, "Specified stat name is not a string");
        String statNameString = statNameExpression.getValueString();
        Expression statValueExpression = Expression.fromScript(statValue, context);
        boolean success = holder.getHolder(context).setStatValue(statNameString, statValueExpression, context);
        if (!success) {
            return new ScriptReturnData(null, false, false, "Stat value could not be set");
        }
        return new ScriptReturnData(null, false, false, null);
    }

}
