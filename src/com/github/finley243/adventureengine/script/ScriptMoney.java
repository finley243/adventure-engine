package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ScriptMoney implements Script {

	private ActorReference actor;
	private int value;
	
	public ScriptMoney(ActorReference actor, int value) {
		this.actor = actor;
		this.value = value;
	}

	@Override
	public void execute(Actor subject) {
		actor.getActor(subject).adjustMoney(value);
	}
	
}
