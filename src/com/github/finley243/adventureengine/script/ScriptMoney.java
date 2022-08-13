package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptMoney extends Script {

	private final ActorReference actor;
	private final int value;
	
	public ScriptMoney(Condition condition, ActorReference actor, int value) {
		super(condition);
		this.actor = actor;
		this.value = value;
	}

	@Override
	public void executeSuccess(Actor subject, Actor target) {
		actor.getActor(subject, target).adjustMoney(value);
	}
	
}
