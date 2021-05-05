package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionMoney implements Condition {

	private int value;

	public ConditionMoney(int value) {
		this.value = value;
	}

	@Override
	public boolean isMet(Actor subject) {
		return subject.getMoney() >= value;
	}
	
	@Override
	public String getChoiceTag() {
		return value + " credits";
	}

}
