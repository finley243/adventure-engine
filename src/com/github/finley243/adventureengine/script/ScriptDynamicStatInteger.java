package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatInt;

public class ScriptDynamicStatInteger extends Script implements ScriptReturnTarget {

    private final StatInt stat;
    private final int base;
    private final int min;
    private final int max;

    public ScriptDynamicStatInteger(StatInt stat, int base, int min, int max) {
        this.stat = stat;
        this.base = base;
        this.min = min;
        this.max = max;
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
        StatInt.StatIntMod currentMod = stat.getMods().get(runtimeStack.getIndex());
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
                StatInt.StatIntMod currentMod = stat.getMods().get(runtimeStack.getIndex());
                runtimeStack.addTempExpressionToMap("add", Expression.constant(currentMod.add()));
                runtimeStack.addTempExpressionToMap("mult", Expression.constant(currentMod.mult()));
            }
            runtimeStack.incrementIndex();
            if (runtimeStack.getIndex() >= stat.getMods().size()) {
                int addSum = 0;
                float multSum = 0.0f;
                for (Expression addExpression : runtimeStack.getTempExpressionsFromMap("add")) {
                    addSum += addExpression.getValueInteger(runtimeStack.getContext());
                }
                for (Expression addExpression : runtimeStack.getTempExpressionsFromMap("mult")) {
                    multSum += addExpression.getValueFloat(runtimeStack.getContext());
                }
                int moddedValue = MathUtils.bound(Math.round(base * (multSum + 1.0f)) + addSum, min, max);
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(moddedValue), false, false, null));
            } else {
                checkNextMod(runtimeStack);
            }
        }
    }

}
