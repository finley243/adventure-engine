package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptBark extends Script {

    private final StatHolderReference actorReference;
    private final String trigger;

    public ScriptBark(StatHolderReference actorReference, String trigger) {
        this.actorReference = actorReference;
        this.trigger = trigger;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        actor.triggerBark(trigger, context);
        sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
    }

}
