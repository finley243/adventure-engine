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
    public ScriptReturnData execute(Context context) {
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        actor.triggerBark(trigger, context);
        return new ScriptReturnData(null, false, false, null);
    }

}
