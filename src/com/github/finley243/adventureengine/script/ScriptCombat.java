package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptCombat extends Script{

    private final ActorReference target;
    private final ActorReference combatant;

    public ScriptCombat(Condition condition, Map<String, Expression> localParameters, ActorReference target, ActorReference combatant) {
        super(condition, localParameters);
        this.target = target;
        this.combatant = combatant;
    }

    @Override
    public void executeSuccess(Context context) {
        if (this.target.getActor(context).getTargetingComponent() != null) {
            this.target.getActor(context).getTargetingComponent().addCombatant(combatant.getActor(context));
        }
    }

}
