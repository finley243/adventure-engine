package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptSetStat extends Script {

    private final StatHolderReference holder;
    private final Script statName;
    private final Script statValue;

    public ScriptSetStat(ScriptTraceData traceData, StatHolderReference holder, Script statName, Script statValue) {
        super(traceData);
        if (holder == null) throw new IllegalArgumentException("ScriptSetState stat holder is null");
        if (statName == null) throw new IllegalArgumentException("ScriptSetState state name is null");
        this.holder = holder;
        this.statName = statName;
        this.statValue = statValue;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData statNameResult = statName.execute(context);
        if (statNameResult.error() != null) {
            return statNameResult;
        } else if (statNameResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain flow statement", getTraceData()));
        }
        Expression statNameExpression = statNameResult.value();
        if (statNameExpression == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is null", getTraceData()));
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is not a string", getTraceData()));
        String statNameString = statNameExpression.getValueString();
        ScriptReturnData statValueResult = statValue.execute(context);
        if (statValueResult.error() != null) {
            return statValueResult;
        } else if (statValueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain flow statement", getTraceData()));
        }
        Expression statValueExpression = statValueResult.value();
        boolean success = holder.getHolder(context).setStatValue(statNameString, statValueExpression, context);
        if (!success) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Stat value could not be set", getTraceData()));
        }
        return new ScriptReturnData(null, null, null);
    }

}
