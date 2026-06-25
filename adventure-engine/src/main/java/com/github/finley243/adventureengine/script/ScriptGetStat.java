package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetStat extends Script {

    private final Script statHolder;
    private final Script statName;

    public ScriptGetStat(ScriptTraceData traceData, Script statHolder, Script statName) {
        super(traceData);
        if (statHolder == null) throw new IllegalArgumentException("ScriptGetStat stat holder reference is null");
        this.statHolder = statHolder;
        this.statName = statName;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptReturnData statHolderResult = statHolder.execute(scriptRuntime, context);
        if (statHolderResult.error() != null) {
            return statHolderResult;
        } else if (statHolderResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression contains unexpected flow statement", getTraceData()));
        } else if (statHolderResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Cannot get value from null holder", getTraceData()));
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
        if (statNameExpression == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified value name is null", getTraceData()));
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is not a string", getTraceData()));
        String statNameString = statNameExpression.getValueString();
        Expression statValue = statHolderValue.getScriptValue(statNameString, context);
        return new ScriptReturnData(statValue, null, null);
    }

}
