package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionMoney extends Condition {

	private final ActorReference actor;
	private final int value;

	public ConditionMoney(boolean invert, ActorReference actor, int value) {
		super(invert);
		this.actor = actor;
		this.value = value;
	}

	@Override
	public boolean isMetInternal(Actor subject) {
		return (actor.getActor(subject).getMoney() >= value);
	}

}
