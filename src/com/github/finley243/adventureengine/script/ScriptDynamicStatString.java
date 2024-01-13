package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatString;

import java.util.List;

public class ScriptDynamicStatString extends Script implements ScriptReturnTarget {

    private final StatString stat;
    private final String base;

    public ScriptDynamicStatString(StatString stat, String base) {
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
        StatString.StatStringMod currentMod = stat.getMods().get(runtimeStack.getIndex());
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
                StatString.StatStringMod currentMod = stat.getMods().get(runtimeStack.getIndex());
                runtimeStack.addTempExpressionToMap("value", Expression.constant(currentMod.value()));
            }
            runtimeStack.incrementIndex();
            if (runtimeStack.getIndex() >= stat.getMods().size()) {
                List<Expression> modValues = runtimeStack.getTempExpressionsFromMap("value");
                Expression moddedValue = modValues == null ? Expression.constant(base) : modValues.getLast();
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(moddedValue, false, false, null));
            } else {
                checkNextMod(runtimeStack);
            }
        }
    }

}
