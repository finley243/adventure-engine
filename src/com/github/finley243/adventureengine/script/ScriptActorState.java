package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;

public class ScriptActorState extends Script {

	private final ActorReference actor;
	private final boolean enabled;

	public ScriptActorState(Condition condition, ActorReference actor, boolean enabled) {
		super(condition);
		this.actor = actor;
		this.enabled = enabled;
	}
	
	@Override
	public void executeSuccess(Actor subject) {
		actor.getActor(subject).setEnabled(enabled);
	}
	
}
