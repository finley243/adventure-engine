package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptCombat extends Script{

    private final ActorReference target;
    private final ActorReference combatant;

    public ScriptCombat(Condition condition, ActorReference target, ActorReference combatant) {
        super(condition);
        this.target = target;
        this.combatant = combatant;
    }

    @Override
    public void executeSuccess(Actor subject) {
        if(target.getActor(subject).targetingComponent() != null) {
            target.getActor(subject).targetingComponent().addCombatant(combatant.getActor(subject));
        }
    }

}
