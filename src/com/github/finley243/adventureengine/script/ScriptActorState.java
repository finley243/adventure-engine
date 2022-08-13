package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptActorState extends Script {

	private final ActorReference actor;
	private final boolean enabled;

	public ScriptActorState(Condition condition, ActorReference actor, boolean enabled) {
		super(condition);
		this.actor = actor;
		this.enabled = enabled;
	}
	
	@Override
	public void executeSuccess(Actor subject, Actor target) {
		actor.getActor(subject, target).setEnabled(enabled);
	}
	
}
