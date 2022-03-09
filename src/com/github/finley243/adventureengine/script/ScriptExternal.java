package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptExternal extends Script {

    private final String scriptID;

    public ScriptExternal(Condition condition, String scriptID) {
        super(condition);
        this.scriptID = scriptID;
    }

    @Override
    protected void executeSuccess(Actor subject) {
        subject.game().data().getScript(scriptID).execute(subject);
    }
}
