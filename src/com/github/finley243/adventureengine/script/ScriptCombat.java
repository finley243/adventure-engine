package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptCombat extends Script{

    private final StatHolderReference actor;
    private final StatHolderReference target;

    public ScriptCombat(Condition condition, StatHolderReference actor, StatHolderReference target) {
        super(condition);
        this.actor = actor;
        this.target = target;
    }

    @Override
    public void executeSuccess(Context context) {
        if (!(actor.getHolder(context) instanceof Actor actorCast) || !(target.getHolder(context) instanceof Actor targetCast)) {
            return;
        }
        actorCast.getTargetingComponent().addCombatant(targetCast);
        context.game().eventQueue().executeNext();
    }

}
