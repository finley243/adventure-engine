package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ScriptCombat implements Script{

    private final ActorReference target;
    private final ActorReference combatant;

    public ScriptCombat(ActorReference target, ActorReference combatant) {
        this.target = target;
        this.combatant = combatant;
    }

    @Override
    public void execute(Actor subject) {
        target.getActor(subject).addCombatTarget(combatant.getActor(subject));
    }

}
