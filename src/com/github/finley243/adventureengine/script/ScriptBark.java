package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptBark extends Script {

    private final StatHolderReference actorReference;
    private final String trigger;

    public ScriptBark(Condition condition, StatHolderReference actorReference, String trigger) {
        super(condition);
        this.actorReference = actorReference;
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        actor.triggerBark(trigger, context);
        sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
    }

}
