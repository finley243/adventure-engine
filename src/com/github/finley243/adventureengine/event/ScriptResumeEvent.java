package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.RuntimeStack;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptReturnTarget;

public class ScriptResumeEvent implements QueuedEvent {

    private final RuntimeStack runtimeStack;
    private final Script.ScriptReturn scriptReturn;

    public ScriptResumeEvent(RuntimeStack runtimeStack, Script.ScriptReturn scriptReturn) {
        this.runtimeStack = runtimeStack;
        this.scriptReturn = scriptReturn;
    }

    @Override
    public void execute(Game game) {
        runtimeStack.getReturnTarget().onScriptReturn(runtimeStack, scriptReturn);
    }

    @Override
    public boolean continueAfterExecution() {
        return false;
    }

}
