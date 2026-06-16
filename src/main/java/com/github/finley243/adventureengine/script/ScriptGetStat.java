package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetStat extends Script {

    private final ScriptValueHolderReference statHolder;
    private final Script statName;

    public ScriptGetStat(ScriptTraceData traceData, ScriptValueHolderReference statHolder, Script statName) {
        super(traceData);
        if (statHolder == null) throw new IllegalArgumentException("ScriptGetStat stat holder reference is null");
        this.statHolder = statHolder;
        this.statName = statName;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptReturnData statNameResult = statName.execute(scriptRuntime, context);
        if (statNameResult.error() != null) {
            return statNameResult;
        } else if (statNameResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        Expression statNameExpression = statNameResult.value();
        if (statNameExpression == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is null", getTraceData()));
        if (statNameExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat name is not a string", getTraceData()));
        String statNameString = statNameExpression.getValueString();
        ScriptValueHolder statHolderValue = statHolder.getHolder(scriptRuntime, context);
        if (statHolderValue == null) return new ScriptReturnData(null, null, new ScriptErrorData("Specified stat holder is null", getTraceData()));
        Expression statValue = statHolderValue.getScriptValue(statNameString, context);
        return new ScriptReturnData(statValue, null, null);
    }

}
