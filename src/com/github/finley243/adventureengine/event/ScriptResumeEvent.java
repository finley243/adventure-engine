package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptReturnTarget;

public class ScriptResumeEvent implements QueuedEvent {

    private final ScriptReturnTarget returnTarget;
    private final Script.ScriptReturn scriptReturn;

    public ScriptResumeEvent(ScriptReturnTarget returnTarget, Script.ScriptReturn scriptReturn) {
        this.returnTarget = returnTarget;
        this.scriptReturn = scriptReturn;
    }

    @Override
    public void execute(Game game) {
        returnTarget.onScriptReturn(scriptReturn);
    }

    @Override
    public boolean continueAfterExecution() {
        return false;
    }

}
