package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatBoolean;

public class ScriptDynamicStatBoolean extends Script implements ScriptReturnTarget {

    private final StatBoolean stat;
    private final boolean base;

    public ScriptDynamicStatBoolean(StatBoolean stat, boolean base) {
        this.stat = stat;
        this.base = base;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        if (stat.getMods().isEmpty()) {
            sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(base), false, false, null));
        } else {
            runtimeStack.addContext(runtimeStack.getContext(), this);
            checkNextMod(runtimeStack);
        }
    }

    private void checkNextMod(RuntimeStack runtimeStack) {
        StatBoolean.StatBooleanMod currentMod = stat.getMods().get(runtimeStack.getIndex());
        if (currentMod.condition() != null) {
            currentMod.condition().execute(runtimeStack);
        } else {
            onScriptReturn(runtimeStack, new ScriptReturnData(Expression.constant(true), false, false, null));
        }
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        if (scriptReturnData.error() != null) {
            throw new IllegalArgumentException("Script threw an error");
        } else if (scriptReturnData.isReturn()) {
            throw new IllegalArgumentException("Script contained an unexpected return statement");
        } else if (scriptReturnData.value() == null) {
            throw new IllegalArgumentException("Script return value is null");
        } else if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.BOOLEAN) {
            throw new IllegalArgumentException("Script return value is not a boolean");
        } else {
            if (scriptReturnData.value().getValueBoolean(runtimeStack.getContext())) {
                StatBoolean.StatBooleanMod currentMod = stat.getMods().get(runtimeStack.getIndex());
                runtimeStack.addTempExpressionToMap("value", Expression.constant(currentMod.value()));
            }
            runtimeStack.incrementIndex();
            if (runtimeStack.getIndex() >= stat.getMods().size()) {
                int countTrue = 0;
                int countFalse = 0;
                for (Expression expression : runtimeStack.getTempExpressionsFromMap("value")) {
                    if (expression.getValueBoolean(runtimeStack.getContext())) {
                        countTrue += 1;
                    } else {
                        countFalse += 1;
                    }
                }
                boolean moddedValue = (countTrue == countFalse) ? base : (countTrue > 0 && countFalse > 0) ? stat.getPriorityValue() : countTrue > 0;
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(moddedValue), false, false, null));
            } else {
                checkNextMod(runtimeStack);
            }
        }
    }

}
