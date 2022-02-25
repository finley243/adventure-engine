package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorDead extends Condition {

	private final ActorReference actor;
	
	public ConditionActorDead(boolean invert, ActorReference actor) {
		super(invert);
		this.actor = actor;
	}

	@Override
	public boolean isMet(Actor subject) {
		return actor.getActor(subject).isDead() != invert;
	}

}
