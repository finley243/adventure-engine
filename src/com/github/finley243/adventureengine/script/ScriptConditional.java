package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.List;

public class ScriptConditional extends Script implements ScriptReturnTarget {

    private final List<ConditionalScriptPair> conditionalScriptPairs;
    private final Script scriptElse;

    public ScriptConditional(List<ConditionalScriptPair> conditionalScriptPairs, Script scriptElse) {
        super(null);
        this.conditionalScriptPairs = conditionalScriptPairs;
        this.scriptElse = scriptElse;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        runtimeStack.addContext(runtimeStack.getContext(), null);
        executeNextIteration(runtimeStack);
    }

    private void executeNextIteration(RuntimeStack runtimeStack) {
        if (runtimeStack.getIndex() >= conditionalScriptPairs.size() + (scriptElse == null ? 0 : 1)) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
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
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturn scriptReturn) {
        runtimeStack.closeContext();
        if (scriptReturn.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (scriptReturn.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (runtimeStack.expressionQueueIsEmpty()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
        } else if (runtimeStack.getIndex() >= conditionalScriptPairs.size() + (scriptElse == null ? 0 : 1)) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
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
