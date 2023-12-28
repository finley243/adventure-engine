package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.RuntimeStack;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptReturnTarget;

public class ScriptEvent implements QueuedEvent, ScriptReturnTarget {

    private final Script script;
    private final Context context;

    public ScriptEvent(Script script, Context context) {
        this.script = script;
        this.context = context;
    }

    @Override
    public void execute(Game game) {
        RuntimeStack runtimeStack = new RuntimeStack();
        runtimeStack.addContext(context, this);
        script.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, Script.ScriptReturn scriptReturn) {
        context.game().eventQueue().startExecution();
    }

    @Override
    public boolean continueAfterExecution() {
        return false;
    }

}
