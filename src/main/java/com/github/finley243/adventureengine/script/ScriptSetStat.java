package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetStat extends Script {

    private final Script holder;
    private final Script statName;
    private final Script statValue;

    public ScriptSetStat(ScriptTraceData traceData, Script holder, Script statName, Script statValue) {
        super(traceData);
        if (holder == null) throw new IllegalArgumentException("ScriptSetState stat holder is null");
        if (statName == null) throw new IllegalArgumentException("ScriptSetState state name is null");
        this.holder = holder;
        this.statName = statName;
        this.statValue = statValue;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptReturnData statHolderResult = holder.execute(scriptRuntime, context);
        if (statHolderResult.error() != null) {
            return statHolderResult;
        } else if (statHolderResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression contains unexpected flow statement", getTraceData()));
        } else if (statHolderResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Cannot assign value on null holder", getTraceData()));
        } else if (statHolderResult.value().getDataType() != Expression.DataType.STAT_HOLDER) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression is not a value holder", getTraceData()));
        }
        ScriptValueHolder statHolderValue = statHolderResult.value().getValueStatHolder();

        ScriptReturnData statNameResult = statName.execute(scriptRuntime, context);
        if (statNameResult.error() != null) {
            return statNameResult;
        } else if (statNameResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression contains unexpected flow statement", getTraceData()));
        }
        Expression statNameExpression = statNameResult.value();
        if (statNameExpression == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is null", getTraceData()));
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is not a string", getTraceData()));
        String statNameString = statNameExpression.getValueString();
        ScriptReturnData statValueResult = statValue.execute(scriptRuntime, context);
        if (statValueResult.error() != null) {
            return statValueResult;
        } else if (statValueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression contains unexpected flow statement", getTraceData()));
        }
        Expression statValueExpression = statValueResult.value();
        boolean success = statHolderValue.setScriptValue(statNameString, statValueExpression, context);
        if (!success) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Stat value could not be set", getTraceData()));
        }
        return new ScriptReturnData(null, null, null);
    }

}
