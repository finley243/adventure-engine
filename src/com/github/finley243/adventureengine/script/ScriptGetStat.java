package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptGetStat extends Script {

    private final StatHolderReference statHolder;
    private final Script statName;

    public ScriptGetStat(int line, StatHolderReference statHolder, Script statName) {
        super(line);
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
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getLine()));
        }
        Expression statNameExpression = statNameResult.value();
        if (statNameExpression == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is null", getLine()));
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is not a string", getLine()));
        String statNameString = statNameExpression.getValueString();
        StatHolder statHolderValue = statHolder.getHolder(context);
        Expression statValue = statHolderValue.getStatValue(statNameString, context);
        return new ScriptReturnData(statValue, null, null);
    }

}
