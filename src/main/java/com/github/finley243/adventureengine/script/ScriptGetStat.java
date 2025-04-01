package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptGetStat extends Script {

    private final StatHolderReference statHolder;
    private final Script statName;

    public ScriptGetStat(ScriptTraceData traceData, StatHolderReference statHolder, Script statName) {
        super(traceData);
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
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        Expression statNameExpression = statNameResult.value();
        if (statNameExpression == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is null", getTraceData()));
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is not a string", getTraceData()));
        String statNameString = statNameExpression.getValueString();
        StatHolder statHolderValue = statHolder.getHolder(context);
        if (statHolderValue == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat holder is null", getTraceData()));
        Expression statValue = statHolderValue.getStatValue(statNameString, context);
        return new ScriptReturnData(statValue, null, null);
    }

}
