package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorDead implements Condition {

	private final ActorReference actor;
	
	public ConditionActorDead(ActorReference actor) {
		this.actor = actor;
	}

	@Override
	public boolean isMet(Actor subject) {
		return actor.getActor(subject).isDead();
	}

	@Override
	public String getChoiceTag() {
		return null;
	}

}
