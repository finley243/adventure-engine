package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptCombat extends Script{

    private final StatHolderReference actorReference;
    private final StatHolderReference targetReference;

    public ScriptCombat(Condition condition, StatHolderReference actorReference, StatHolderReference targetReference) {
        super(condition);
        this.actorReference = actorReference;
        this.targetReference = targetReference;
    }

    @Override
    public void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        if (!(targetReference.getHolder(context) instanceof Actor target)) throw new IllegalArgumentException("Target reference is not a valid actor");
        actor.getTargetingComponent().addCombatant(target);
        sendReturn(new ScriptReturn(null, false, false, null));
    }

}
