package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ScriptEvent;

public class ScriptExternal extends Script {

    private final String scriptID;

    public ScriptExternal(Condition condition, String scriptID) {
        super(condition);
        this.scriptID = scriptID;
    }

    @Override
    protected void executeSuccess(Context context) {
        Context innerContext = new Context(context);
        context.game().eventQueue().addToFront(new ScriptEvent(context.game().data().getScript(scriptID), innerContext));
        context.game().eventQueue().executeNext();
    }

}
