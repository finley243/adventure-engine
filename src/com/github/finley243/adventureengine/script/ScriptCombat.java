package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ScriptCombat extends Script{

    private final ActorReference target;
    private final ActorReference combatant;

    public ScriptCombat(Condition condition, Map<String, Variable> localParameters, ActorReference target, ActorReference combatant) {
        super(condition, localParameters);
        this.target = target;
        this.combatant = combatant;
    }

    @Override
    public void executeSuccess(ContextScript context) {
        if (this.target.getActor(context).getTargetingComponent() != null) {
            this.target.getActor(context).getTargetingComponent().addCombatant(combatant.getActor(context));
        }
    }

}
