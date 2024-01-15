package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptGetStat extends Script {

    private final StatHolderReference statHolder;
    private final Script statName;

    public ScriptGetStat(StatHolderReference statHolder, Script statName) {
        this.statHolder = statHolder;
        this.statName = statName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression statNameExpression = Expression.fromScript(statName, context);
        if (statNameExpression == null) return new ScriptReturnData(null, false, false, "Specified stat name is null");
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, false, false, "Specified stat name is not a string");
        String statNameString = statNameExpression.getValueString();
        Expression statValue = statHolder.getHolder(context).getStatValue(statNameString, context);
        return new ScriptReturnData(statValue, false, false, null);
    }

}
