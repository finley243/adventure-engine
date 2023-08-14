package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.Script;

public class ScriptEvent implements QueuedEvent {

    private final Script script;
    private final Context context;

    public ScriptEvent(Script script, Context context) {
        this.script = script;
        this.context = context;
    }

    @Override
    public void execute(Game game) {
        script.execute(context);
    }

}
