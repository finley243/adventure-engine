package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Actor.Attribute;

public class ConditionAttribute implements Condition {

	private Attribute attribute;
	private int value;
	
	public ConditionAttribute(Attribute attribute, int value) {
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public boolean isMet(Actor subject) {
		return subject.getAttribute(attribute) >= value;
	}

	@Override
	public String getChoiceTag() {
		return value + " " + attribute.toString();
	}

}
