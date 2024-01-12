package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptCombat extends Script{

    private final StatHolderReference actorReference;
    private final StatHolderReference targetReference;

    public ScriptCombat(StatHolderReference actorReference, StatHolderReference targetReference) {
        this.actorReference = actorReference;
        this.targetReference = targetReference;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        if (!(targetReference.getHolder(context) instanceof Actor target)) throw new IllegalArgumentException("Target reference is not a valid actor");
        actor.getTargetingComponent().addCombatant(target);
        sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
    }

}
