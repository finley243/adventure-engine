package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionMoney implements Condition {

	private final ActorReference actor;
	private final int value;

	public ConditionMoney(ActorReference actor, int value) {
		this.actor = actor;
		this.value = value;
	}

	@Override
	public boolean isMet(Actor subject) {
		return actor.getActor(subject).getMoney() >= value;
	}
	
	@Override
	public String getChoiceTag() {
		if(actor.isPlayer()) {
			return value + " credits";
		}
		return null;
	}

}
