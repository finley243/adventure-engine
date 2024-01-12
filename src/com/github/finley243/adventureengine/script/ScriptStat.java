package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptStat extends Script implements ScriptReturnTarget {

    private final StatHolderReference statHolder;
    private final Expression statName;

    public ScriptStat(StatHolderReference statHolder, Expression statName) {
        this.statHolder = statHolder;
        this.statName = statName;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        if (statName.getDataType(runtimeStack.getContext()) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptDynamicStat statName is not a string");
        String statNameValue = statName.getValueString(runtimeStack.getContext());
        Script statScript = statHolder.getHolder(runtimeStack.getContext()).getStatValue(statNameValue, runtimeStack.getContext());
        runtimeStack.addContext(runtimeStack.getContext(), this);
        statScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        runtimeStack.closeContext();
        if (scriptReturnData.error() != null) {
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression cannot contain a return statement"));
        } else {
            sendReturn(runtimeStack, scriptReturnData);
        }
    }

}
