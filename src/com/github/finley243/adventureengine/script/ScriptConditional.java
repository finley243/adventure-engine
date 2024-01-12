package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.List;

public class ScriptConditional extends Script implements ScriptReturnTarget {

    private final List<ConditionalScriptPair> conditionalScriptPairs;
    private final Script scriptElse;

    public ScriptConditional(List<ConditionalScriptPair> conditionalScriptPairs, Script scriptElse) {
        this.conditionalScriptPairs = conditionalScriptPairs;
        this.scriptElse = scriptElse;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        runtimeStack.addContext(runtimeStack.getContext(), null);
        executeNextIteration(runtimeStack);
    }

    private void executeNextIteration(RuntimeStack runtimeStack) {
        if (runtimeStack.getIndex() >= conditionalScriptPairs.size() + (scriptElse == null ? 0 : 1)) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
        } else if (runtimeStack.getIndex() == conditionalScriptPairs.size() && scriptElse != null) {
            runtimeStack.incrementIndex();
            Context innerContext = new Context(runtimeStack.getContext(), true);
            runtimeStack.addContext(innerContext, this);
            scriptElse.execute(runtimeStack);
        } else {
            ConditionalScriptPair currentBranch = conditionalScriptPairs.get(runtimeStack.getIndex());
            runtimeStack.incrementIndex();
            if (currentBranch.condition.isMet(runtimeStack.getContext())) {
                Context innerContext = new Context(runtimeStack.getContext(), true);
                runtimeStack.addContext(innerContext, this);
                currentBranch.script.execute(runtimeStack);
            } else {
                executeNextIteration(runtimeStack);
            }
        }
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        runtimeStack.closeContext();
        if (scriptReturnData.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (runtimeStack.expressionQueueIsEmpty()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
        } else if (runtimeStack.getIndex() >= conditionalScriptPairs.size() + (scriptElse == null ? 0 : 1)) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
        } else {
            executeNextIteration(runtimeStack);
        }
    }

    public static class ConditionalScriptPair {
        private final Condition condition;
        private final Script script;

        public ConditionalScriptPair(Condition condition, Script script) {
            this.condition = condition;
            this.script = script;
        }
    }

}
