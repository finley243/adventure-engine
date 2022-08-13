package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptAlertState extends Script {

    private final ActorReference actor;
    private final TargetingComponent.AlertState alertState;

    public ScriptAlertState(Condition condition, ActorReference actor, TargetingComponent.AlertState alertState) {
        super(condition);
        this.actor = actor;
        this.alertState = alertState;
    }

    @Override
    protected void executeSuccess(Actor subject, Actor target) {
        if (actor.getActor(subject, target).targetingComponent() != null) {
            actor.getActor(subject, target).targetingComponent().setAlertState(alertState);
        }
    }

}
