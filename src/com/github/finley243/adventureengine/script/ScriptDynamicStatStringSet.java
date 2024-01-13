package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatStringSet;

import java.util.HashSet;
import java.util.Set;

public class ScriptDynamicStatStringSet extends Script implements ScriptReturnTarget {

    private final StatStringSet stat;
    private final Set<String> base;

    public ScriptDynamicStatStringSet(StatStringSet stat, Set<String> base) {
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
        StatStringSet.StatStringSetMod currentMod = stat.getMods().get(runtimeStack.getIndex());
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
                StatStringSet.StatStringSetMod currentMod = stat.getMods().get(runtimeStack.getIndex());
                runtimeStack.addTempExpressionToMap("add", Expression.constant(currentMod.addition()));
                runtimeStack.addTempExpressionToMap("remove", Expression.constant(currentMod.cancellation()));
            }
            runtimeStack.incrementIndex();
            if (runtimeStack.getIndex() >= stat.getMods().size()) {
                Set<String> outputSet = new HashSet<>(base);
                for (Expression expression : runtimeStack.getTempExpressionsFromMap("add")) {
                    Set<String> addValues = expression.getValueStringSet(runtimeStack.getContext());
                    outputSet.addAll(addValues);
                }
                for (Expression expression : runtimeStack.getTempExpressionsFromMap("remove")) {
                    Set<String> removeValues = expression.getValueStringSet(runtimeStack.getContext());
                    outputSet.removeAll(removeValues);
                }
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(outputSet), false, false, null));
            } else {
                checkNextMod(runtimeStack);
            }
        }
    }

}
