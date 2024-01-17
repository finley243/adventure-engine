package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptGetStat extends Script {

    private final StatHolderReference statHolder;
    private final Script statName;

    public ScriptGetStat(StatHolderReference statHolder, Script statName) {
        if (statHolder == null) throw new IllegalArgumentException("ScriptGetStat stat holder reference is null");
        this.statHolder = statHolder;
        this.statName = statName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData statNameResult = statName.execute(context);
        if (statNameResult.error() != null) {
            return statNameResult;
        } else if (statNameResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Expression cannot contain a flow statement");
        }
        Expression statNameExpression = statNameResult.value();
        if (statNameExpression == null) return new ScriptReturnData(null, null, "Specified stat name is null");
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Specified stat name is not a string");
        String statNameString = statNameExpression.getValueString();
        StatHolder statHolderValue = statHolder.getHolder(context);
        Expression statValue = statHolderValue.getStatValue(statNameString, context);
        return new ScriptReturnData(statValue, null, null);
    }

}
