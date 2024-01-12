package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.RuntimeStack;
import com.github.finley243.adventureengine.script.Script;

public class ScriptResumeEvent implements QueuedEvent {

    private final RuntimeStack runtimeStack;
    private final Script.ScriptReturnData scriptReturnData;

    public ScriptResumeEvent(RuntimeStack runtimeStack, Script.ScriptReturnData scriptReturnData) {
        this.runtimeStack = runtimeStack;
        this.scriptReturnData = scriptReturnData;
    }

    @Override
    public void execute(Game game) {
        runtimeStack.getReturnTarget().onScriptReturn(runtimeStack, scriptReturnData);
    }

    @Override
    public boolean continueAfterExecution() {
        return false;
    }

}
