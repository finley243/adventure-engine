package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptBark extends Script {

    // List of possible lines to be selected
    private final ActorReference actor;
    private final String trigger;

    public ScriptBark(Condition condition, ActorReference actor, String trigger) {
        super(condition);
        this.actor = actor;
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        actor.getActor(context).triggerBark(trigger, context.getTarget());
    }

}
